package com.ms.paymentservice.model;

import java.util.Arrays;

public enum PaymentMethod {

    CREDIT_CARD {
        @Override
        public boolean canBeProcessed() {
            return true; // Cartão de crédito processado imediatamente
        }

    },
    DEBIT_CARD {
        @Override
        public boolean canBeProcessed() {
            return true; // Cartão de débito processado imediatamente
        }

    },
    PIX {
        @Override
        public boolean canBeProcessed() {
            return true; // PIX processado imediatamente
        }

    },
    BOLETO {
        @Override
        public boolean canBeProcessed() {
            return false; // Boleto não pode ser processado imediatamente
        }
    },
    PAYPAL {
        @Override
        public boolean canBeProcessed() {
            return true; // PayPal processado imediatamente
        }
    };

    // Método que define se o pagamento pode ser processado imediatamente
    public abstract boolean canBeProcessed();

    // Método para converter string para enum
    public static PaymentMethod fromString(String paymentMethod) {
        try {
            return valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String validMethods = Arrays.toString(PaymentMethod.values());
            throw new IllegalArgumentException("Método de pagamento inválido: " + paymentMethod + ". Métodos válidos: " + validMethods);
        }
    }
}

