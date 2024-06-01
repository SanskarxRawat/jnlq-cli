package com.jnlqcli.commands.sort;


import picocli.CommandLine.*;

@Command(name = "sort", description = "Sort the JSON data based on a key")
final public class JsonSortCommand {

    @Option(names = {"-k", "--key"}, description = "The key to sort on")
    String key;
    @Option(names = {"-o", "--order"}, description = "The sort order (asc or desc)")
    String order;
}
