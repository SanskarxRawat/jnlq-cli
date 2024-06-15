package com.services.impl;

import com.beans.ObjectMapperBean;
import com.constants.JsonConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.services.JsonService;
import com.utils.ConsoleUtil;
import com.utils.JsonUtil;
import jakarta.inject.Singleton;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

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
            ConsoleUtil.printInfo("Matching Nodes:");
            ConsoleUtil.printJson(matchingNodes);
        }
    }

    @Override
    public void filter(String key, String value) throws JsonProcessingException {
        importedJson = ObjectMapperBean.getInstance().readTree(DEFAULT_JSON);
        JsonNode filteredData = JsonUtil.filterByKeyValue(importedJson, key, value);
        ConsoleUtil.printInfo("Filtered data:");
        ConsoleUtil.printJson(filteredData);

    }

    @Override
    public void sort(String key, String order) {
        JsonNode sortedData = JsonUtil.sortByKey(importedJson, key, order.equalsIgnoreCase("desc"));
        ConsoleUtil.printInfo("Sorted data:");
        ConsoleUtil.printJson(sortedData);
    }

    @Override
    public void prettify(String jsonInput) {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        assert node != null;
        String prettified=node.toPrettyString();
        ConsoleUtil.printInfo(prettified);
    }

    @Override
    public void convert(String jsonInput, String format,String outputFilePath) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);

        switch (format.toLowerCase()){
            case JsonConstants.CSV:
                JsonUtil.convertToCSV(node,outputFilePath);
                break;
            case JsonConstants.XML:
                JsonUtil.convertToXML(node,outputFilePath);
                break;
            case JsonConstants.YAML:
                JsonUtil.convertToYAML(node,outputFilePath);
                break;
            case JsonConstants.IMAGE:
                JsonUtil.convertToImage(node,outputFilePath);
                break;
            case JsonConstants.PDF:
                JsonUtil.convertToPDF(node,outputFilePath);
                break;
            default:
                throw new IllegalArgumentException("Invalid Output Format");
        }

    }

    @Override
    public void validate(String jsonInput, String schemaFile) {
        JsonNode data=JsonUtil.parseJsonInput(jsonInput);
        JsonNode schema=JsonUtil.parseJsonInput(schemaFile);

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema jsonSchema = factory.getSchema(schema);
        Set<ValidationMessage> validationResult = jsonSchema.validate(data);

        if (validationResult.isEmpty()) {
            ConsoleUtil.printInfo("JSON data is valid according to the schema.");
        } else {
            ConsoleUtil.printError("JSON data is invalid. Validation errors:");
            for (ValidationMessage message : validationResult) {
                ConsoleUtil.printError(message.toString());
            }
        }
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
        return JsonUtil.unflattenJson(node);
    }

    @Override
    public void query(String jsonInput, String path) {
        JsonNode node=JsonUtil.parseJsonInput(jsonInput);
        JsonNode result=JsonUtil.queryWithJsonPath(node,path);
        ConsoleUtil.printJson(result);
    }

    @Override
    public JsonNode merge(List<String> jsonInputs) {
        List<JsonNode> nodes=jsonInputs.stream()
                .map(JsonUtil::parseJsonInput)
                .toList();

        return JsonUtil.mergeNodes(nodes);
    }
}
