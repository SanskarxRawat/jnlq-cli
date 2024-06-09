package com.utils;

import com.beans.ObjectMapperBean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Singleton
public class JsonUtil {

    public static JsonNode searchByKeyValue(JsonNode jsonData,String key,String value){
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
        
    }

}
