package com.jnlqcli.commands.search;

import picocli.CommandLine.*;

@Command(name = "search", description = "Search for a specific value in the JSON data")
final public class JsonSearchCommand implements Runnable {

    @Option(names = {"-k", "--key"}, description = "The key to search for")
    String key;
    @Option(names = {"-v", "--value"}, description = "The value to search for")
    String value;

    @Override
    public void run() {
        
    }
}
