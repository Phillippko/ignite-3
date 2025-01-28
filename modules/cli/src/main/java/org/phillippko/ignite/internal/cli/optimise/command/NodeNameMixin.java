package org.phillippko.ignite.internal.cli.optimise.command;

import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_DESC;
import static org.apache.ignite.internal.cli.commands.Options.Constants.NODE_NAME_OPTION_SHORT;

import picocli.CommandLine.Option;

public class NodeNameMixin {
    @Option(names = {NODE_NAME_OPTION, NODE_NAME_OPTION_SHORT}, description = NODE_NAME_OPTION_DESC)
    private String nodeName;

    public String nodeName() {
        return nodeName;
    }
}
