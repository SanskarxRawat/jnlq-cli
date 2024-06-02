package com.jnlqcli;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JnlqCliCommandTest {

    @Test
    public void testWithCommandLineOption() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out=System.out;
        System.setOut(new PrintStream(baos));

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            String[] args = new String[] { "import" ,"-p=/workspaces/jnlq-cli/src/main/resources/data.json","--verbose"};
            PicocliRunner.run(JnlqCliCommand.class, ctx, args);
            out.println();
            baos.toString();
            // jnlq-cli
        }
    }
}
