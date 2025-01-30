package com.ecommerce.notificationservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SwaggerConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testCustomOpenAPIBean() {
        OpenAPI openAPI = context.getBean("customOpenAPI", OpenAPI.class);
        assertNotNull(openAPI);
    }
}