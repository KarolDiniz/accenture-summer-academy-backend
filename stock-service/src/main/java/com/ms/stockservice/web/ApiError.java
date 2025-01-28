package com.ms.stockservice.web;

import java.time.LocalDateTime;

record ApiError(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
