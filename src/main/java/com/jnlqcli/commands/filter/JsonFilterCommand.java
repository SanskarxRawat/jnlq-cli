package com.jnlqcli.commands.filter;


import picocli.CommandLine.*;

@Command(name = "filter", description = "Filter the JSON data based on a key and value")
final public class JsonFilterCommand implements Runnable {

    @Option(names = {"-k", "--key"}, description = "The key to filter on")
    String key;

    @Option(names = {"-v", "--value"}, description = "The value to filter on")
    String value;

    @Override
    public void run() {

    }
}
