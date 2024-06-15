package com.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public interface JsonService {

    void importJson(String filePath) throws IOException;

    JsonNode getImportedJson();

    void search(String key,String value);

    void filter(String key,String value) throws JsonProcessingException;

    void sort(String key,String order);

    void prettify(String jsonInput);

    void convert(String jsonInput,String format,String outputFilePath) throws IOException, ParserConfigurationException, TransformerException, SAXException;

    void validate(String jsonInput,String schemaFile);

    JsonNode flatten(String jsonInput);

    JsonNode unflatten(String jsonInput);

    void query(String jsonInput,String path);

    JsonNode merge(List<String> jsonInputs);
}
