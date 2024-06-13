package com.utils;

import com.beans.ObjectMapperBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.github.underscore.U;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Singleton
public class JsonUtil {

    private static String jsonString;

    public static JsonNode searchByKeyValue(JsonNode jsonData, String key, String value){
        ArrayNode results = JsonNodeFactory.instance.arrayNode();
        if (jsonData.isArray()) {
            for (JsonNode element : jsonData) {
                JsonNode match = searchByKeyValue(element, key, value);
                if (!match.isMissingNode()) {
                    results.add(match);
                }
            }
        } else if (jsonData.isObject()) {
            if (jsonData.has(key) && jsonData.get(key).asText().equals(value)) {
                results.add(jsonData);
            } else {
                for (JsonNode child : jsonData) {
                    JsonNode match = searchByKeyValue(child, key, value);
                    if (!match.isMissingNode()) {
                        results.add(match);
                    }
                }
            }
        }
        return results;
    }

    public static JsonNode filterByKeyValue(JsonNode node, String key, String value) {
        ArrayNode filteredArray = JsonNodeFactory.instance.arrayNode();
        if (node.isArray()) {
            for (JsonNode element : node) {
                JsonNode filtered = filterByKeyValue(element, key, value);
                if (!filtered.isMissingNode()) {
                    filteredArray.add(filtered);
                }
            }
        } else if (node.isObject()) {
            if (node.has(key) && node.get(key).asText().equals(value)) {
                filteredArray.add(node);
            } else {
                ObjectNode filteredObject = JsonNodeFactory.instance.objectNode();
                boolean matched = false;
                for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    JsonNode filtered = filterByKeyValue(entry.getValue(), key, value);
                    if (!filtered.isMissingNode()) {
                        filteredObject.set(entry.getKey(), filtered);
                        matched = true;
                    }
                }
                if (matched) {
                    filteredArray.add(filteredObject);
                }
            }
        }
        return filteredArray;
    }

    public static JsonNode sortByKey(JsonNode node, String key, boolean descending) {
        if (node.isArray()) {
            List<JsonNode> sortedList = new ArrayList<>();
            for (JsonNode element : node) {
                sortedList.add(element);
            }
            sortedList.sort((a, b) -> {
                String valueA = a.get(key).asText();
                String valueB = b.get(key).asText();
                int comparison = valueA.compareTo(valueB);
                return descending ? -comparison : comparison;
            });
            ArrayNode sortedArray = JsonNodeFactory.instance.arrayNode();
            for (JsonNode element : sortedList) {
                sortedArray.add(element);
            }
            return sortedArray;
        } else {
            return node;
        }
    }

    public static JsonNode parseJsonInput(String input){
        try {
            File inputFile=new File(input);
            if(inputFile.exists() && inputFile.isFile()){
                return ObjectMapperBean.getInstance().readTree(inputFile);
            }
            return new ObjectMapper().readTree(input);
        } catch (IOException e) {
            System.err.println("Error parsing JSON input: " + e.getMessage());
            return null;        }
    }

    public static JsonNode mergeNodes(List<JsonNode> nodes){
        if(CollectionUtils.isEmpty(nodes)){
            return ObjectMapperBean.getInstance().createObjectNode();
        }
        JsonNode firstNode= nodes.getFirst();
        if(firstNode.isArray()){
            mergeArrays(nodes);
        }else if(firstNode.isObject()){
            mergeObjects(nodes);
        }
        throw new IllegalArgumentException("Cannot merge array with non-array JSON node");
    }

    private static JsonNode mergeArrays(List<JsonNode> nodes){
        ArrayNode mergedArray=ObjectMapperBean.getInstance().createArrayNode();
        for (JsonNode node : nodes) {
            if (!node.isArray()) {
                throw new IllegalArgumentException("Cannot merge array with non-array JSON node");
            }
            mergedArray.addAll((ArrayNode) node);
        }
        return mergedArray;
    }

    private static JsonNode mergeObjects(List<JsonNode> nodes){
        ObjectNode mergedObject = ObjectMapperBean.getInstance().createObjectNode();
        for (JsonNode node : nodes) {
            if (!node.isObject()) {
                throw new IllegalArgumentException("Cannot merge object with non-object JSON node");
            }
            ObjectNode objNode = (ObjectNode) node;
            objNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (mergedObject.has(key)) {
                    JsonNode existingValue = mergedObject.get(key);
                    if (existingValue.isObject() && value.isObject()) {
                        mergedObject.set(key, mergeObjects(List.of(existingValue, value)));
                    } else if (existingValue.isArray() && value.isArray()) {
                        mergedObject.set(key, mergeArrays(List.of(existingValue, value)));
                    } else {
                        mergedObject.set(key, value);
                    }
                } else {
                    mergedObject.set(key, value);
                }
            });
        }
        return mergedObject;
    }

    public static void flattenJsonRecursive(JsonNode node, ObjectNode result, String prefix){
        if(node.isObject()){
            ObjectNode objectNode=(ObjectNode) node;
            Iterator<String> fieldNames= objectNode.fieldNames();
            while(fieldNames.hasNext()){
                String fieldName=fieldNames.next();
                JsonNode fieldValue=objectNode.get(fieldName);
                String newPrefix= StringUtils.isEmpty(prefix)?fieldName:prefix+"."+fieldName;
                flattenJsonRecursive(fieldValue,result,newPrefix);
            }
        } else if(node.isArray()){
            for(int index=0;index<node.size();index++){
                String newPrefix=prefix+"["+index+"]";
                flattenJsonRecursive(node.get(index),result,newPrefix);
            }
        }else{
            result.set(prefix,node);
        }
    }

    public static JsonNode unflattenJson(JsonNode node){
        if(!node.isObject()){
            throw new IllegalArgumentException("Input must be a JSON object");
        }
        ObjectNode result=ObjectMapperBean.getInstance().createObjectNode();

        node.fields().forEachRemaining(
                entry->{
                    String key=entry.getKey();
                    JsonNode value=entry.getValue();
                    setNestedValue(result, key.split("\\."), value);
                }
        );

        return result;
    }

    private static void setNestedValue(ObjectNode parent, String[] path, JsonNode value){
        ObjectNode current=parent;
        Pattern arrayPattern=Pattern.compile("(.*?)\\[(\\d+)]");

        for (int i = 0; i < path.length; i++) {
            String segment = path[i];
            Matcher matcher = arrayPattern.matcher(segment);

            if (matcher.matches()) {
                String arrayName = matcher.group(1);
                int arrayIndex = Integer.parseInt(matcher.group(2));

                if (!current.has(arrayName) || !current.get(arrayName).isArray()) {
                    current.set(arrayName, ObjectMapperBean.getInstance().createArrayNode());
                }

                ArrayNode arrayNode = (ArrayNode) current.get(arrayName);
                while (arrayNode.size() <= arrayIndex) {
                    arrayNode.addObject();
                }

                if (i == path.length - 1) {
                    arrayNode.set(arrayIndex, value);
                } else {
                    current = (ObjectNode) arrayNode.get(arrayIndex);
                }
            } else {
                if (i == path.length - 1) {
                    current.set(segment, value);
                } else {
                    if (!current.has(segment) || !current.get(segment).isObject()) {
                        current.set(segment, ObjectMapperBean.getInstance().createObjectNode());
                    }
                    current = (ObjectNode) current.get(segment);
                }
            }
        }

    }

    public static JsonNode queryWithJsonPath(JsonNode node, String jsonPath) {
        Configuration conf = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .options(Option.ALWAYS_RETURN_LIST)
                .build();

        List<JsonNode> result = JsonPath.using(conf).parse(node.toString()).read(jsonPath);

        if (result.size() == 1) {
            return result.getFirst();
        } else {
            ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
            arrayNode.addAll(result);
            return arrayNode;
        }
    }

    public static void convertToCSV(JsonNode node) throws IOException {
        try {
            CsvSchema csvSchema = createCsvSchema(node);
            CsvMapper csvMapper = new CsvMapper();

            String csv = csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValueAsString(node);

            String downloadDir = System.getProperty("user.home") + File.separator + "Downloads";

            String fileName = "converted_" + System.currentTimeMillis() + ".csv";
            Path filePath = Paths.get(downloadDir, fileName);

            Files.write(filePath, csv.getBytes());

            ConsoleUtil.printInfo("CSV file downloaded to: " + filePath);
        } catch (IOException e) {
            throw new IOException("Unable to covert Json to CSV");
        }
    }

    public static void convertToXML(JsonNode node) {
        String xmlString = U.jsonToXml(jsonString);
    }

    public static void convertToYAML(JsonNode node) {

    }

    public static void convertToImage(JsonNode node) {

    }

    public static void convertToPDF(JsonNode node) {
        
    }

    private static CsvSchema createCsvSchema(JsonNode node) {
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = node.elements().next();
        firstObject.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
        return csvSchemaBuilder.build().withHeader();
    }

}
