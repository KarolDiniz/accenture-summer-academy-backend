package com.ms.paymentservice.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PaymentMethodTest {

    @Test
    void shouldReturnTrueForProcessableMethods() {
        assertTrue(PaymentMethod.CREDIT_CARD.canBeProcessed(), "CREDIT_CARD deveria ser processável");
        assertTrue(PaymentMethod.DEBIT_CARD.canBeProcessed(), "DEBIT_CARD deveria ser processável");
        assertTrue(PaymentMethod.PIX.canBeProcessed(), "PIX deveria ser processável");
    }

    @Test
    void shouldReturnFalseForNonProcessableMethods() {
        assertFalse(PaymentMethod.BOLETO.canBeProcessed(), "BOLETO não deveria ser processável imediatamente");
    }

    @Test
    void shouldConvertValidStringToEnum() {
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("credit_card"));
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.fromString("debit_card"));
        assertEquals(PaymentMethod.PIX, PaymentMethod.fromString("pix"));
        assertEquals(PaymentMethod.BOLETO, PaymentMethod.fromString("boleto"));
    }

    @Test
    void shouldConvertValidStringWithDifferentCaseToEnum() {
        assertEquals(PaymentMethod.CREDIT_CARD, PaymentMethod.fromString("Credit_Card"));
        assertEquals(PaymentMethod.DEBIT_CARD, PaymentMethod.fromString("dEbIt_CaRd"));
        assertEquals(PaymentMethod.PIX, PaymentMethod.fromString("PIX"));
        assertEquals(PaymentMethod.BOLETO, PaymentMethod.fromString("BoLeTo"));
    }

    @Test
    void shouldThrowExceptionForInvalidPaymentMethod() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            PaymentMethod.fromString("invalid_method")
        );

        assertTrue(exception.getMessage().contains("Método de pagamento inválido"));
        assertTrue(exception.getMessage().contains("CREDIT_CARD, DEBIT_CARD, PIX, BOLETO"));
    }
}
