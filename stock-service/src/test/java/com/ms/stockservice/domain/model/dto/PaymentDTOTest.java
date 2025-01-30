package com.ms.stockservice.domain.model.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentDTOTest {

    @Test
    void testPaymentDTOConstructor() {
        PaymentDTO paymentDTO = new PaymentDTO(123L, "PENDING");
        assertNotNull(paymentDTO);
        assertEquals(123L, paymentDTO.getOrderId());
        assertEquals("PENDING", paymentDTO.getStatus());
    }

    @Test
    void testOrderItemDTO() {
        PaymentDTO.OrderItemDTO itemDTO = new PaymentDTO.OrderItemDTO();
        itemDTO.setSku("SKU123");
        itemDTO.setQuantity(2);
        assertEquals("SKU123", itemDTO.getSku());
        assertEquals(2, itemDTO.getQuantity());
    }
}
