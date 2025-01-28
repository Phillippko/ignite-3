package org.phillippko.ignite.internal.cli.optimise.command;

import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_DESC;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_SHORT;
import static org.apache.ignite.internal.cli.commands.Options.Constants.WRITE_INTENSIVE_OPTION;

import jakarta.inject.Inject;
import java.util.concurrent.Callable;
import org.phillippko.ignite.internal.cli.optimise.call.RunOptimiseCall;
import org.phillippko.ignite.internal.cli.optimise.call.RunOptimiseCallInput;
import org.apache.ignite.internal.cli.commands.BaseCommand;
import org.apache.ignite.internal.cli.commands.cluster.ClusterUrlMixin;
import org.apache.ignite.internal.cli.core.call.CallExecutionPipeline;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "runOptimise", description = "Optimise cluster configuration.")
public class RunOptimiseCommand extends BaseCommand implements Callable<Integer> {
    /** Node URL option. */
    @Mixin
    private ClusterUrlMixin clusterUrlMixin;

    @Inject
    private RunOptimiseCall call;

    @Mixin
    private OptimiseMixin optimiseMixin;

    @Mixin
    private NodeNameMixin nodeName;

    @Override
    public Integer call() {
        return runPipeline(CallExecutionPipeline.builder(call)
                .inputProvider(this::buildCallInput)
                .output(spec.commandLine().getOut())
        );
    }

    private RunOptimiseCallInput buildCallInput() {
        return RunOptimiseCallInput.builder()
                .setClusterUrl(clusterUrlMixin.getClusterUrl())
                .setWriteIntensive(optimiseMixin.writeIntensive())
                .setTunerTimeout(optimiseMixin.tunerTimeout())
                .setNodeName(nodeName.nodeName())
                .build();
    }
}
