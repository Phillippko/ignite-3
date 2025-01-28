package org.phillippko.ignite.internal.cli.optimise.command;

import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_DESC;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_SHORT;

import jakarta.inject.Inject;
import java.util.concurrent.Callable;
import org.apache.ignite.internal.cli.commands.BaseCommand;
import org.apache.ignite.internal.cli.commands.cluster.ClusterUrlMixin;
import org.apache.ignite.internal.cli.commands.questions.ConnectToClusterQuestion;
import org.apache.ignite.internal.cli.core.call.CallExecutionPipeline;
import org.apache.ignite.internal.cli.core.exception.handler.ClusterNotInitializedExceptionHandler;
import org.apache.ignite.internal.cli.core.flow.builder.Flows;
import org.phillippko.ignite.internal.cli.optimise.call.RunBenchmarkCall;
import org.phillippko.ignite.internal.cli.optimise.call.RunBenchmarkCallInput;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command that shows configuration from the cluster.
 */
@Command(name = "runBenchmark", description = "Shows node configuration")
public class RunBenchmarkReplCommand extends BaseCommand implements Runnable {
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

    @Mixin
    private NodeNameMixin nodeName;

    @Option(names = "--iterations", description = "Number of iterations to run the benchmark.", defaultValue = "1")
    private int iterations;

    @Option(names = "--values", description = "Number of values to run the benchmark.")
    private Long values;

    @Option(names = "--profile", description = "Storage profile to use for the benchmark.", defaultValue = "default")
    private String profile;

    @Inject
    private ConnectToClusterQuestion question;

    @Override
    public void run() {
        runFlow(question.askQuestionIfNotConnected(clusterUrlMixin.getClusterUrl())
                .map(this::buildCallInput)
                .then(Flows.fromCall(call))
                .exceptionHandler(ClusterNotInitializedExceptionHandler.createReplHandler("Cannot get results"))
                .print()
        );
    }

    private RunBenchmarkCallInput buildCallInput(String url) {
        return RunBenchmarkCallInput.builder()
                .setClusterUrl(url)
                .setBenchmarkFilePath(benchmarkFilePath)
                .setNodeName(nodeName.nodeName())
                .setIterations(iterations)
                .setValues(values)
                .setProfile(profile)
                .build();
    }
}
