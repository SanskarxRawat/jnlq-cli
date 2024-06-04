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
            String[] args = new String[] { "filter","-key=name","-v=John Doe"};
            PicocliRunner.run(JnlqCliCommand.class, ctx, args);
            out.println();
            baos.toString();
            // jnlq-cli
            assertTrue(baos.toString().contains("Filtered data:"));

            String expectedOutput= """
            {
              "name": "John Doe",
              "age": 30,
              "email": "john.doe@example.com",
              "addresses": [
                {
                  "street": "123 Main St",
                  "city": "Anytown",
                  "state": "CA",
                  "zip": "12345"
                },
                {
                  "street": "456 Oak Rd",
                  "city": "Elsewhere",
                  "state": "NY",
                  "zip": "67890"
                }
              ]
            }""";
            System.out.println(baos.toString());
            assertTrue(baos.toString().contains(expectedOutput));

        }
    }
}
