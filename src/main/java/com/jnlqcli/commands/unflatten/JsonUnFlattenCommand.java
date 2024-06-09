package com.jnlqcli.commands.unflatten;

import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "unflatten", description = "UnFlatten nested JSON data")
public class JsonUnFlattenCommand implements Runnable{

    @Parameters(paramLabel = "INPUT", description = "JSON input (file or string)")
    private String jsonInput;

    @Inject
    private JsonService jsonService;


    @Override
    public void run() {
        
    }
}
