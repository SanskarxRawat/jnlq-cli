package com.services.impl;

import com.beans.ObjectMapperBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.services.JsonService;
import com.utils.JsonUtil;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Singleton
public class JsonServiceImpl implements JsonService {
    private static final String DEFAULT_JSON = """
            {
              "name": "Sanskar",
              "age": 30,
              "email": "john.doe@example.com",
              "addresses": [
                {
                  "street": "123 Main St",
                  "city": "Anytown",
                  "state": "CA",
                  "zip": "12345"
                },
                {
                  "street": "456 Oak Rd",
                  "city": "Elsewhere",
                  "state": "NY",
                  "zip": "67890"
                }
              ]
            }""";
    private JsonNode importedJson;
    @Override
    public void importJson(String filePath) throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
        importedJson = ObjectMapperBean.getInstance().readTree(new String(jsonData, StandardCharsets.UTF_8));
    }

    @Override
    public JsonNode getImportedJson() {
        return importedJson;
    }

    @Override
    public void search(String key, String value) {

        JsonNode matchingNodes=JsonUtil.searchByKeyValue(importedJson,key,value);
        if(matchingNodes.isArray()){
            matchingNodes.forEach(System.out::println);
        }else {
            System.out.println("Matching Nodes: "+matchingNodes);
        }
    }

    @Override
    public void filter(String key, String value) throws JsonProcessingException {
        importedJson = ObjectMapperBean.getInstance().readTree(DEFAULT_JSON);
        JsonNode filteredData = JsonUtil.filterByKeyValue(importedJson, key, value);
        System.out.println("Filtered data:");
        System.out.println(filteredData.toPrettyString());

    }

    @Override
    public void sort(String key, String order) {
        JsonNode sortedData = JsonUtil.sortByKey(importedJson, key, order.equalsIgnoreCase("desc"));
        System.out.println("Sorted data:");
        System.out.println(sortedData.toPrettyString());
    }

    @Override
    public void prettify(String jsonInput) {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        assert node != null;
        String prettified=node.toPrettyString();
        System.out.println(prettified);
    }

    @Override
    public void convert(String jsonInput, String format) {

        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        
    }

    @Override
    public void validate(String jsonInput, String schemaFile) {

    }

    @Override
    public JsonNode flatten(String jsonInput) {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        ObjectNode result=ObjectMapperBean.getInstance().createObjectNode();
        JsonUtil.flattenJsonRecursive(node,result,"");
        return result;
    }

    @Override
    public JsonNode unflatten(String jsonInput) {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        JsonNode result=JsonUtil.unflattenJson(node);
    }

    @Override
    public void query(String jsonInput, String path) {

    }

    @Override
    public JsonNode merge(List<String> jsonInputs) {
        List<JsonNode> nodes=jsonInputs.stream()
                .map(JsonUtil::parseJsonInput)
                .toList();

        return JsonUtil.mergeNodes(nodes);
    }
}
