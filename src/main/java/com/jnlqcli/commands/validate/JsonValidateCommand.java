package com.jnlqcli.commands.validate;

import picocli.CommandLine.*;

@Command(name = "validate", description = "Validate JSON data against a schema")
public class JsonValidateCommand implements Runnable{

    @Parameters(paramLabel = "INPUT", description = "JSON input (file or string)")
    private String jsonInput;

    @Option(names = {"-s", "--schema"}, description = "JSON schema file", required = true)
    private String schemaFile;


    @Override
    public void run() {

    }
}
