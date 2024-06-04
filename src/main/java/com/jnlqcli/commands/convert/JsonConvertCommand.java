package com.jnlqcli.commands.convert;

import picocli.CommandLine.*;

@Command(name = "convert", description = "Convert JSON to other formats")
public class JsonConvertCommand {

    @Parameters(paramLabel = "INPUT", description = "JSON input (file or string)")
    private String jsonInput;

    @Option(names = {"-f", "--format"}, description = "Output format (csv, xml, yaml, image, pdf)")
    private String outputFormat;
}
