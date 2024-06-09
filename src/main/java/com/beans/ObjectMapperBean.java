package com.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperBean {
    private static final ObjectMapper INSTANCE = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }

    private ObjectMapperBean() {
        // Private constructor to prevent instantiation
    }
}