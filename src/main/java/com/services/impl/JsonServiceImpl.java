package com.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.JsonService;
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
}
