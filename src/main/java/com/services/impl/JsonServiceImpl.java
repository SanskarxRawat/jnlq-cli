package com.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.JsonService;
import com.utils.JsonUtil;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Singleton
public class JsonServiceImpl implements JsonService {

    private JsonNode importedJson;
    @Override
    public void importJson(String filePath) throws IOException {
        final ObjectMapper objectMapper=new ObjectMapper();
        byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
        importedJson = objectMapper.readTree(new String(jsonData, StandardCharsets.UTF_8));
    }

    @Override
    public JsonNode getImportedJson() {
        return importedJson;
    }

    @Override
    public void search(String key, String value) {

        JsonNode matchingNodes= JsonUtil.searchByKeyValue(importedJson,key,value);
        if(matchingNodes.isArray()){
            matchingNodes.forEach(System.out::println);
        }else {
            System.out.println("Matching Nodes: "+matchingNodes);
        }
    }

    @Override
    public void filter(String key, String value) {
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
}
