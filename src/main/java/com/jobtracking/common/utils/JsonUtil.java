package com.jobtracking.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Centralized JSON utility to eliminate duplicate JSON operations
 * Provides consistent JSON parsing and stringification
 */
@Component
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * Parse JSON string to object
     */
    public <T> T parseJson(String json, Class<T> type) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convert object to JSON string
     */
    public String toJson(Object object) {
        if (object == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Parse JSON string to Map
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> parseToMap(String json) {
        return parseJson(json, Map.class);
    }

    /**
     * Parse JSON string to JsonNode for flexible access
     */
    public JsonNode parseToNode(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON to node: " + e.getMessage(), e);
        }
    }

    /**
     * Get string value from JSON node safely
     */
    public String getStringValue(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName)) {
            return null;
        }
        
        String value = node.get(fieldName).asText(null);
        return (value != null && value.trim().isEmpty()) ? null : value;
    }

    /**
     * Check if JSON string is valid
     */
    public boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}