package com.ms.stockservice.web;

import java.time.LocalDateTime;
import java.util.Map;

record ValidationError(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
