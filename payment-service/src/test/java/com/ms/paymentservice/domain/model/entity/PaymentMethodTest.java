package com.ms.paymentservice.domain.model.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void shouldReturnCorrectProcessingStatus() {
        assertTrue(PaymentMethod.CREDIT_CARD.canBeProcessed());
        assertTrue(PaymentMethod.DEBIT_CARD.canBeProcessed());
        assertTrue(PaymentMethod.PIX.canBeProcessed());
        assertFalse(PaymentMethod.BOLETO.canBeProcessed());
    }

    @Test
    void shouldConvertStringToEnumSuccessfully() {
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("CREDIT_CARD"));
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.fromString("DEBIT_CARD"));
        assertEquals(PaymentMethod.PIX, PaymentMethod.fromString("PIX"));
        assertEquals(PaymentMethod.BOLETO, PaymentMethod.fromString("BOLETO"));
    }

    @Test
    void shouldThrowExceptionForInvalidPaymentMethod() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PaymentMethod.fromString("INVALID_METHOD");
        });

        String expectedMessage = "Método de pagamento inválido: INVALID_METHOD";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
