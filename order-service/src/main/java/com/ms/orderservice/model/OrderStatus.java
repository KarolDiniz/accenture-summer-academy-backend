package com.ms.orderservice.model;

import java.util.Arrays;

public enum OrderStatus {

    PENDING {
        @Override
        public boolean canTransitionTo(OrderStatus nextStatus) {
            return nextStatus == CONFIRMED || nextStatus == CANCELLED;
        }
    },
    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderStatus nextStatus) {
            return nextStatus == SHIPPING;
        }
    },
    SHIPPING {
        @Override
        public boolean canTransitionTo(OrderStatus nextStatus) {
            return nextStatus == DELIVERED; // Uma vez que o pedido foi enviado, ele só pode ser entregue
        }
    },
    DELIVERED {
        @Override
        public boolean canTransitionTo(OrderStatus nextStatus) {
            return false; // Estado final
        }
    },
    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderStatus nextStatus) {
            return false; // Estado final
        }
    };

    public abstract boolean canTransitionTo(OrderStatus nextStatus);


    // Implementa um padrão Factory Method com o método estático, que cria uma instância de OrderStatus a partir de uma string
    public static OrderStatus fromString(String status) {

        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String validStates = Arrays.toString(OrderStatus.values());
            throw new IllegalArgumentException("Invalid order status: " + status + ". Valid states: " + validStates);
        }
    }

    public boolean isTerminalState() {
        return this == CANCELLED || this == DELIVERED;
    }
}
