package com.services;


import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface JsonService {

    void importJson(String filePath) throws IOException;

    JsonNode getImportedJson();
}
