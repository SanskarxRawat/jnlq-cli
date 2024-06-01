package com.services;


import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface JsonService {

    void importJson(String filePath) throws IOException;

    JsonNode getImportedJson();

    void search(String key,String value);

    void filter(String key,String value);

    void sort(String key,String order);
}
