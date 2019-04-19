package com.bastronaut.bigspender.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonResponseUtil {

    final private static ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode getJsonFromResponseContent(String responseContent) throws IOException {
        return objectMapper.readTree(responseContent);
    }
}
