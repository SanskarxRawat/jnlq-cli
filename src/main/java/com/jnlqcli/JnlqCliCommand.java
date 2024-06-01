package com.jnlqcli;

import com.jnlqcli.commands.importer.JsonImportCommand;
import io.micronaut.configuration.picocli.PicocliRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jnlq-cli", description = "...",
        mixinStandardHelpOptions = true,subcommands = {JsonImportCommand.class})
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
