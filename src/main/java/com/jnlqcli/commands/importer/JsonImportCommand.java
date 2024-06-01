package com.jnlqcli.commands.importer;

import com.services.JsonService;
import jakarta.inject.Inject;
import picocli.CommandLine.*;

@Command(name = "import", description = "Import a JSON file for querying")
final public class JsonImportCommand implements Runnable{

    @Inject
    JsonService jsonService;

    @Option(names = {"-p","--path"}, description = "The path of the JSON file to import")
    String filePath;

    @Option(names = {"--verbose"},description = "Print verbose output")
    boolean verbose;


    @Override
    public void run() {
        System.out.println("Import Command Running");
        try {
            jsonService.importJson(filePath);
            System.out.println("JSON file imported successfully from: " + filePath);
        } catch (Exception e) {
            System.err.println("Failed to import JSON file: " + e.getMessage());
        }
    }
}
