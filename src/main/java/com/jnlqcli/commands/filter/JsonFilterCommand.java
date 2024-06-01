package com.jnlqcli.commands.filter;


import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "filter", description = "Filter the JSON data based on a key and value")
final public class JsonFilterCommand implements Runnable {

    @Option(names = {"-k", "--key"}, description = "The key to filter on")
    private String key;

    @Option(names = {"-v", "--value"}, description = "The value to filter on")
    private String value;

    @Inject
    private JsonService jsonService;

    @Override
    public void run() {
        jsonService.filter(key,value);
    }
}
