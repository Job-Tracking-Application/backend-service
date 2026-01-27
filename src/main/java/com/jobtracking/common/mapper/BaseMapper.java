package com.jobtracking.common.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple base mapper with basic utility methods
 * Easy to understand for fresher developers
 */
public class BaseMapper {

    /**
     * Check if object is null - simple null safety
     */
    protected boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * Check if object is not null
     */
    protected boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * Convert list safely - returns empty list if null
     */
    protected <T> List<T> safeList(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    /**
     * Clean string - trim and handle empty strings
     */
    protected String cleanString(String str) {
        if (str == null) {
            return null;
        }
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}