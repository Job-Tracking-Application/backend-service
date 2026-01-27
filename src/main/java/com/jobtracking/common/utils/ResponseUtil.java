package com.jobtracking.common.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jobtracking.common.response.ApiResponse;

/**
 * Utility class for creating consistent API responses
 * Eliminates duplicate response creation patterns
 */
public class ResponseUtil {

    /**
     * Create successful response with data
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "Operation successful");
    }

    /**
     * Create successful response with data and custom message
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.ok(response);
    }

    /**
     * Create successful response with custom status
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Create error response with message
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Create error response with message and status
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Create unauthorized response
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Create forbidden response
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Create not found response
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Create conflict response
     */
    public static <T> ResponseEntity<ApiResponse<T>> conflict(String message) {
        return error(message, HttpStatus.CONFLICT);
    }

    /**
     * Create internal server error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create no content response
     */
    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}