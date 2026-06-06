package com.clubsphere.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utility class for JSON operations
 */
public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
    
    /**
     * Parse JSON from request body into specified class
     * @param request The HTTP request containing JSON body
     * @param clazz The class to parse JSON into
     * @param <T> Type parameter
     * @return Object of type T parsed from JSON
     * @throws IOException if reading request fails
     */
    public static <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String requestBody = sb.toString();
        logger.debug("Parsed request body: {}", requestBody);
        
        return gson.fromJson(requestBody, clazz);
    }
    
    /**
     * Convert object to JSON string
     * @param obj Object to convert to JSON
     * @return JSON string representation of the object
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * Parse JSON string into specified class
     * @param json JSON string to parse
     * @param clazz The class to parse JSON into
     * @param <T> Type parameter
     * @return Object of type T parsed from JSON
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
public class JsonUtil {
    
}
