package com.utils;

import com.beans.ObjectMapperBean;
import com.fasterxml.jackson.databind.JsonNode;

public class ConsoleUtil {

    public static void printJson(JsonNode jsonNode) {
        try {
            printInfo(ObjectMapperBean.getInstance().writeValueAsString(jsonNode));
        } catch (Exception e) {
            printError("Error printing JSON: " + e.getMessage());
        }
    }

    public static void printError(String message) {
        System.err.println("Error: " + message);
    }

    public static void printInfo(String message) {
        System.out.println(message);
    }
}
