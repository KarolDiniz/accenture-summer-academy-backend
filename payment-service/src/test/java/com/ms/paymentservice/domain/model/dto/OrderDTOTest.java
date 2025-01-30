package com.ms.paymentservice.domain.model.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ms.paymentservice.domain.model.entity.PaymentMethod;

class OrderDTOTest {

    @Test
    void shouldCreateOrderDTOAndVerifyValues() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(150.75));
        order.setPaymentMethod(PaymentMethod.DEBIT_CARD);

        OrderDTO.OrderItemDTO item1 = new OrderDTO.OrderItemDTO();
        item1.setSku("ITEM123");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(50.25));

        order.setItems(List.of(item1));

        assertEquals(1L, order.getId());
        assertEquals("customer@example.com", order.getCustomerEmail());
        assertEquals(BigDecimal.valueOf(150.75), order.getTotalAmount());
        assertEquals(PaymentMethod.DEBIT_CARD, order.getPaymentMethod());
        assertNotNull(order.getItems());
        assertEquals(1, order.getItems().size());

        OrderDTO.OrderItemDTO retrievedItem = order.getItems().get(0);
        assertEquals("ITEM123", retrievedItem.getSku());
        assertEquals(2, retrievedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(50.25), retrievedItem.getPrice());
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        OrderDTO order1 = new OrderDTO();
        order1.setId(1L);
        order1.setCustomerEmail("customer@example.com");

        OrderDTO order2 = new OrderDTO();
        order2.setId(1L);
        order2.setCustomerEmail("customer@example.com");

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());

        order2.setCustomerEmail("other@example.com");
        assertNotEquals(order1, order2);
    }

    @Test
    void shouldVerifyToString() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(150.75));

        String result = order.toString();
        assertTrue(result.contains("customer@example.com"));
        assertTrue(result.contains("150.75"));
    }
}
