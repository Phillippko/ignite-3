package org.phillippko.ignite.internal.cli.optimise.call;

import javax.annotation.Nullable;
import org.apache.ignite.internal.cli.core.call.CallInput;

public class RunBenchmarkCallInput implements CallInput {
    private final String clusterUrl;

    private final String benchmarkFilePath;
    private final String nodeName;
    private final int iterations;
    private final @Nullable Long values;
    private final String profile;

    public RunBenchmarkCallInput(
            String clusterUrl,
            @Nullable String nodeName,
            @Nullable String benchmarkFilePath,
            int iterations,
            @Nullable Long values,
            String profile
    ) {
        this.clusterUrl = clusterUrl;
        this.benchmarkFilePath = benchmarkFilePath;
        this.nodeName = nodeName;
        this.iterations = iterations;
        this.values = values;
        this.profile = profile;
    }

    public static RunBenchmarkCallInputBuilder builder() {
        return new RunBenchmarkCallInputBuilder();
    }

    public String getClusterUrl() {
        return clusterUrl;
    }

    public @Nullable String getBenchmarkFilePath() {
        return benchmarkFilePath;
    }

    public @Nullable String getNodeName() {
        return nodeName;
    }

    public int getIterations() {
        return iterations;
    }

    public @Nullable Long getValues() {
        return values;
    }

    public String getProfile() {
        return profile;
    }

    public static class RunBenchmarkCallInputBuilder {
        private String clusterUrl;
        private @Nullable String benchmarkFilePath;
        private @Nullable String nodeName;
        private int iterations;
        private @Nullable Long values;
        private String profile;

        public RunBenchmarkCallInputBuilder setClusterUrl(String clusterUrl) {
            this.clusterUrl = clusterUrl;

            return this;
        }

        public RunBenchmarkCallInputBuilder setBenchmarkFilePath(String benchmarkFilePath) {
            this.benchmarkFilePath = benchmarkFilePath;

            return this;
        }

        public RunBenchmarkCallInputBuilder setNodeName(String nodeName) {
            this.nodeName = nodeName;

            return this;
        }

        public RunBenchmarkCallInputBuilder setIterations(int iterations) {
            this.iterations = iterations;

            return this;
        }

        public RunBenchmarkCallInputBuilder setValues(@Nullable Long values) {
            this.values = values;

            return this;
        }

        public RunBenchmarkCallInputBuilder setProfile(String profile) {
            this.profile = profile;

            return this;
        }

        public RunBenchmarkCallInput build() {
            return new RunBenchmarkCallInput(clusterUrl, nodeName, benchmarkFilePath, iterations, values, profile);
        }
    }
}
