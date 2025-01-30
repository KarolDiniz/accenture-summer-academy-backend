package com.ms.orderservice.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentDTOTest {

    @Test
    void testPaymentDTO() {
        // Arrange
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(1L);
        paymentDTO.setStatus("APPROVED");
        paymentDTO.setPaymentMethod("CREDIT_CARD");

        // Act & Assert
        assertNotNull(paymentDTO);
        assertEquals(1L, paymentDTO.getOrderId());
        assertEquals("APPROVED", paymentDTO.getStatus());
        assertEquals("CREDIT_CARD", paymentDTO.getPaymentMethod());
    }

    @Test
    void testPaymentDTOConstructor() {
        // Arrange
        PaymentDTO paymentDTO = new PaymentDTO(1L, "APPROVED", "CREDIT_CARD");

        // Act & Assert
        assertNotNull(paymentDTO);
        assertEquals(1L, paymentDTO.getOrderId());
        assertEquals("APPROVED", paymentDTO.getStatus());
        assertEquals("CREDIT_CARD", paymentDTO.getPaymentMethod());
    }
}