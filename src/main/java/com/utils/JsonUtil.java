package com.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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

}
