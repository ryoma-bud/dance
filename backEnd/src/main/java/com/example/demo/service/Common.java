package com.example.demo.service;

import org.springframework.util.StringUtils;

public final class Common {

    private Common() {
    }

    public static String normalizeNullableText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }
}