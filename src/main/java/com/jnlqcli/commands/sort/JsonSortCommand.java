package com.jnlqcli.commands.sort;


import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "sort", description = "Sort the JSON data based on a key")
final public class JsonSortCommand implements Runnable{

    @Option(names = {"-k", "--key"}, description = "The key to sort on")
    private String key;
    @Option(names = {"-o", "--order"}, description = "The sort order (asc or desc)")
    private String order;

    @Inject
    private JsonService jsonService;

    @Override
    public void run() {
        jsonService.sort(key,order);
    }
}
