package org.phillippko.ignite.internal.optimiser;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.ignite.configuration.NamedListView;
import org.apache.ignite.internal.configuration.ConfigurationRegistry;
import org.apache.ignite.internal.logger.IgniteLogger;
import org.apache.ignite.internal.logger.Loggers;
import org.apache.ignite.internal.pagememory.configuration.schema.PersistentPageMemoryProfileView;
import org.apache.ignite.internal.pagememory.configuration.schema.VolatilePageMemoryProfileView;
import org.apache.ignite.internal.schema.configuration.GcConfiguration;
import org.apache.ignite.internal.schema.configuration.GcExtensionConfiguration;
import org.apache.ignite.internal.schema.configuration.LowWatermarkConfiguration;
import org.apache.ignite.internal.storage.configurations.StorageExtensionConfiguration;
import org.apache.ignite.internal.storage.configurations.StorageProfileView;
import org.apache.ignite.internal.storage.rocksdb.configuration.schema.RocksDbProfileView;

public class OptimiseRunnerImpl implements OptimiseRunner {
    private static final IgniteLogger LOG = Loggers.forClass(OptimiseRunnerImpl.class);

    private static final String OPENTUNER_COMMAND = "C:/IdeaProjects/apachetuner -timeToRun=%d";

    private static final Path OPENTUNER_RESULT_PATH = Path.of("C:/tmp/opentuner");

    private final ConfigurationRegistry clusterConfigurationRegistry;
    private final ConfigurationRegistry nodeConfigurationRegistry;

    public OptimiseRunnerImpl(ConfigurationRegistry clusterConfigurationRegistry, ConfigurationRegistry nodeConfigurationRegistry) {
        this.clusterConfigurationRegistry = clusterConfigurationRegistry;
        this.nodeConfigurationRegistry = nodeConfigurationRegistry;
    }

    @Override
    public String getIssues(boolean writeIntensive, int tunerTimeout) {
        LOG.info("Running optimisation");

        List<String> issues = new ArrayList<>();
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double freeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        boolean closeToMaxMemory = freeMemory / Runtime.getRuntime().maxMemory() > 0.9;

        NamedListView<StorageProfileView> profiles = nodeConfigurationRegistry.getConfiguration(StorageExtensionConfiguration.KEY).storage()
                .profiles().value();

        if (closeToMaxMemory) {
            issues.add("More than 90% of maximum memory is used, increase -Xmx JVM option");
        } else {
            issues.add("Maximum memory OK");
        }

        GcConfiguration gcConfig = clusterConfigurationRegistry.getConfiguration(GcExtensionConfiguration.KEY).gc();

        if (closeToMaxMemory && gcConfig.threads().value() < Runtime.getRuntime().availableProcessors() / 2) {
            issues.add(
                    "Less than half of available threads designated for GC and memory usage is high, increase gcConfiguration.threads, current: "
                            + gcConfig.threads());
        } else {
            issues.add("GC threads OK");
        }

        LowWatermarkConfiguration lowWatermarkConfiguration = gcConfig.lowWatermark();
        if (lowWatermarkConfiguration.dataAvailabilityTime().value() > 1_000_000) {
            issues.add("Low watermark data availability is too high, causing extra disk usage: "
                    + lowWatermarkConfiguration.dataAvailabilityTime());
        } else {
            issues.add("Low watermark data availability OK");
        }

        if (lowWatermarkConfiguration.updateInterval().value() > lowWatermarkConfiguration.dataAvailabilityTime().value() / 2) {
            issues.add(
                    "Low watermark update interval is too high [updateInterval=" +
                            lowWatermarkConfiguration.updateInterval() +
                            ", dataAvailabilityTime=" +
                            lowWatermarkConfiguration.dataAvailabilityTime() + "]"
            );
        } else {
            issues.add("Low watermark update interval OK");
        }

        long volatileDataRegionsSize = profiles.stream()
                .filter(VolatilePageMemoryProfileView.class::isInstance)
                .map(VolatilePageMemoryProfileView.class::cast)
                .mapToLong(VolatilePageMemoryProfileView::maxSize)
                .sum();

        if (volatileDataRegionsSize > heapMemoryUsage.getMax()) {
            issues.add("Sum of volatile data regions exceeds max heap size in bytes ("
                    + heapMemoryUsage.getMax() + "): " + volatileDataRegionsSize);
        } else {
            issues.add("Volatile data regions size OK");
        }

        if (writeIntensive) {
            String persistentProfiles = profiles.stream()
                    .filter(PersistentPageMemoryProfileView.class::isInstance)
                    .map(StorageProfileView::name).collect(Collectors.joining());

            if (!persistentProfiles.isEmpty()) {
                issues.add("For write-intensive workloads RocksDB should be used instead page memory in these profiles: "
                        + persistentProfiles);
            }
        } else {
            String rocksDbProfiles = profiles.stream()
                    .filter(RocksDbProfileView.class::isInstance)
                    .map(StorageProfileView::name).collect(Collectors.joining());

            if (!rocksDbProfiles.isEmpty()) {
                issues.add(
                        "For read-intensive workloads page memory should be used instead of RocksDB in these profiles: " + rocksDbProfiles
                );
            }
        }

        String opentunerIssues = getIssuesWithOpentuner(tunerTimeout);

        if (!opentunerIssues.isEmpty()) {
            issues.add(opentunerIssues);
        }

        String result = String.join("; ", issues);

        LOG.info(result);

        return result;
    }

    private static String getIssuesWithOpentuner(int tunerTimeout) {
        try {
            ProcessBuilder builder = new ProcessBuilder(String.format(OPENTUNER_COMMAND, tunerTimeout));

            if (builder.start().waitFor(tunerTimeout + 20, TimeUnit.SECONDS)) {
                return Files.readString(OPENTUNER_RESULT_PATH);
            } else {
                return "Opentuner didn't finish in time";
            }

        } catch (IOException | InterruptedException e) {
            LOG.error("Couldn't run opentuner: " + OPENTUNER_COMMAND, e);

            return "Couldn't run opentuner: " + e.getMessage();
        }
    }
}
