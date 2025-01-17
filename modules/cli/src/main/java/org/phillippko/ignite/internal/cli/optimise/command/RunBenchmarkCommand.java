package org.phillippko.ignite.internal.cli.optimise.command;

import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_DESC;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_SHORT;

import jakarta.inject.Inject;
import java.util.concurrent.Callable;
import org.phillippko.ignite.internal.cli.optimise.call.RunBenchmarkCall;
import org.phillippko.ignite.internal.cli.optimise.call.RunBenchmarkCallInput;
import org.apache.ignite.internal.cli.commands.BaseCommand;
import org.apache.ignite.internal.cli.commands.cluster.ClusterUrlMixin;
import org.apache.ignite.internal.cli.core.call.CallExecutionPipeline;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command that shows configuration from the cluster.
 */
@Command(name = "runBenchmark", description = "Shows node configuration")
public class RunBenchmarkCommand extends BaseCommand implements Callable<Integer> {
    /** Node URL option. */
    @Mixin
    private ClusterUrlMixin clusterUrlMixin;

    @Inject
    private RunBenchmarkCall call;

    @Parameters(
            arity = "0..1",
            index = "0",
            description = "Path to the file containing SQL queries to run during benchmark."
    )
    private String benchmarkFilePath;

    @Option(names = {NODE_NAME_OPTION, NODE_NAME_OPTION_SHORT}, description = NODE_NAME_OPTION_DESC)
    private String nodeName;

    @Option(names = "--iterations", description = "Number of iterations to run the benchmark.", defaultValue = "1")
    private int iterations;

    @Option(names = "--values", description = "Number of values to run the benchmark.")
    private Long values;

    @Option(names = "--profile", description = "Storage profile to use for the benchmark.", defaultValue = "default")
    private String profile;

    @Override
    public Integer call() {
        return runPipeline(CallExecutionPipeline.builder(call)
                .inputProvider(this::buildCallInput)
                .output(spec.commandLine().getOut())
        );
    }

    private RunBenchmarkCallInput buildCallInput() {
        return RunBenchmarkCallInput.builder()
                .setClusterUrl(clusterUrlMixin.getClusterUrl())
                .setBenchmarkFilePath(benchmarkFilePath)
                .setNodeName(nodeName)
                .setIterations(iterations)
                .setValues(values)
                .setProfile(profile)
                .build();
    }
}
