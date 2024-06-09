package com.jnlqcli.commands.flatten;

import com.fasterxml.jackson.databind.JsonNode;
import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "flatten", description = "Flatten nested JSON data")
public class JsonFlattenCommand implements Runnable{

    @Parameters(paramLabel = "INPUT", description = "JSON input (file or string)")
    private String jsonInput;

    @Inject
    private JsonService jsonService;

    @Override
    public void run() {
        System.out.println("Flatten Command Running");
        JsonNode flattenNode=jsonService.flatten(jsonInput);
        System.out.println(flattenNode.toPrettyString());
    }
}
