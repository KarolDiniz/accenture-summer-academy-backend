package com.ms.stockservice.presentation.web;

import java.time.LocalDateTime;

record ApiError(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
