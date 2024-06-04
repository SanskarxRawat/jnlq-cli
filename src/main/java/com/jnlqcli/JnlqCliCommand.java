package com.jnlqcli;

import com.jnlqcli.commands.filter.JsonFilterCommand;
import com.jnlqcli.commands.importer.JsonImportCommand;
import com.jnlqcli.commands.search.JsonSearchCommand;
import com.jnlqcli.commands.sort.JsonSortCommand;
import io.micronaut.configuration.picocli.PicocliRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jnlq-cli", description = "...",
        mixinStandardHelpOptions = true,subcommands = {JsonImportCommand.class, JsonFilterCommand.class, JsonSortCommand.class, JsonSearchCommand.class})
public class JnlqCliCommand implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(JnlqCliCommand.class, args);
    }

    public void run() {
        // business logic here
        if (verbose) {
            System.out.println("Hi!");
        }
    }
}
