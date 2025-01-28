package org.phillippko.ignite.internal.cli.optimise.command;

import static org.apache.ignite.internal.cli.commands.Options.Constants.TUNER_TIMEOUT_OPTION;
import static org.apache.ignite.internal.cli.commands.Options.Constants.WRITE_INTENSIVE_OPTION;

import picocli.CommandLine.Option;

public class OptimiseMixin {
    @Option(names = WRITE_INTENSIVE_OPTION, description = "If target configuration should be prepared for write-intensive use-cases")
    private boolean writeIntensive;

    @Option(names = TUNER_TIMEOUT_OPTION, description = "How long should OpenTuner run in milliseconds", defaultValue = "0")
    private int tunerTimeout;

    public boolean writeIntensive() {
        return writeIntensive;
    }

    public int tunerTimeout() {
        return tunerTimeout;
    }
}
