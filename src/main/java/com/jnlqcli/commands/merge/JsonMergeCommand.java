package com.jnlqcli.commands.merge;

import com.fasterxml.jackson.databind.JsonNode;
import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

import java.util.List;

@Command(name = "merge", description = "Merge multiple JSON inputs")
public class JsonMergeCommand implements Runnable{

    @Parameters(paramLabel = "INPUTS", description = "JSON inputs (files or strings)", arity = "1..*")
    private List<String> jsonInputs;

    @Inject
    private JsonService jsonService;


    @Override
    public void run() {
        System.out.println("Merge Command Running!!");
        JsonNode mergedNode=jsonService.merge(jsonInputs);
        System.out.println(mergedNode.toPrettyString());
    }
}
