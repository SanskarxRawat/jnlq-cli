package com.jnlqcli.commands.search;

import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "search", description = "Search for a specific value in the JSON data")
final public class JsonSearchCommand implements Runnable {

    @Option(names = {"-k", "--key"}, description = "The key to search for")
    private String key;
    @Option(names = {"-v", "--value"}, description = "The value to search for")
    private String value;

    @Inject
    private JsonService jsonService;

    @Override
    public void run() {
        jsonService.search(key,value);
    }
}
