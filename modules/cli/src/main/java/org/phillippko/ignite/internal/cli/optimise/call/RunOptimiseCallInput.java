package org.phillippko.ignite.internal.cli.optimise.call;

import org.apache.ignite.internal.cli.core.call.CallInput;

public class RunOptimiseCallInput implements CallInput {
    private final String clusterUrl;
    private final String nodeName;
    private final boolean writeIntensive;
    private final int tunerTimeout;

    public RunOptimiseCallInput(String clusterUrl, String nodeName, boolean writeIntensive, int tunerTimeout) {
        this.clusterUrl = clusterUrl;
        this.nodeName = nodeName;
        this.writeIntensive = writeIntensive;
        this.tunerTimeout = tunerTimeout;
    }

    public static RunOptimiseCallInputBuilder builder() {
        return new RunOptimiseCallInputBuilder();
    }

    public String getClusterUrl() {
        return clusterUrl;
    }

    public int getTunerTimeout() {
        return tunerTimeout;
    }

    public boolean getWriteIntensive() {
        return writeIntensive;
    }

    public String getNodeName() {
        return nodeName;
    }

    public static class RunOptimiseCallInputBuilder {
        private String clusterUrl;
        private String nodeName;
        private boolean writeIntensive;
        private int tunerTimeout;

        public RunOptimiseCallInputBuilder setClusterUrl(String clusterUrl) {
            this.clusterUrl = clusterUrl;

            return this;
        }

        public RunOptimiseCallInputBuilder setNodeName(String nodeName) {
            this.nodeName = nodeName;

            return this;
        }

        public RunOptimiseCallInputBuilder setWriteIntensive(boolean writeIntensive) {
            this.writeIntensive = writeIntensive;

            return this;
        }

        public RunOptimiseCallInputBuilder setTunerTimeout(int tunerTimeout) {
            this.tunerTimeout = tunerTimeout;

            return this;
        }

        public RunOptimiseCallInput build() {
            return new RunOptimiseCallInput(clusterUrl, nodeName, writeIntensive, tunerTimeout);
        }
    }
}
