package com.ms.paymentservice.domain.model.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDTOTest {

    @Test
    void shouldCreatePaymentDTOAndVerifyValues() {
        PaymentDTO payment = new PaymentDTO();
        payment.setOrderId(100L);
        payment.setPaymentDate(LocalDateTime.of(2025, 1, 29, 12, 0));
        payment.setStatus("APPROVED");
        payment.setPaymentMethod("DEBIT_CARD");

        PaymentDTO.OrderItemDTO item = new PaymentDTO.OrderItemDTO();
        item.setSku("ITEM123");
        item.setQuantity(2);

        payment.setItems(List.of(item));

        assertEquals(100L, payment.getOrderId());
        assertEquals(LocalDateTime.of(2025, 1, 29, 12, 0), payment.getPaymentDate());
        assertEquals("APPROVED", payment.getStatus());
        assertEquals("DEBIT_CARD", payment.getPaymentMethod());
        assertNotNull(payment.getItems());
        assertEquals(1, payment.getItems().size());

        PaymentDTO.OrderItemDTO retrievedItem = payment.getItems().get(0);
        assertEquals("ITEM123", retrievedItem.getSku());
        assertEquals(2, retrievedItem.getQuantity());
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        PaymentDTO payment1 = new PaymentDTO();
        payment1.setOrderId(100L);
        payment1.setStatus("APPROVED");

        PaymentDTO payment2 = new PaymentDTO();
        payment2.setOrderId(100L);
        payment2.setStatus("APPROVED");

        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());

        payment2.setStatus("FAILED");
        assertNotEquals(payment1, payment2);
    }

    @Test
    void shouldVerifyToString() {
        PaymentDTO payment = new PaymentDTO();
        payment.setOrderId(100L);
        payment.setStatus("APPROVED");

        String result = payment.toString();
        assertTrue(result.contains("100"));
        assertTrue(result.contains("APPROVED"));
    }
}
