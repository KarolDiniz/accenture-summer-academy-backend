package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;
import com.ms.paymentservice.model.entity.Payment;
import com.ms.paymentservice.model.entity.PaymentMethod;
import com.ms.paymentservice.model.exception.InvalidPaymentMethodException;
import com.ms.paymentservice.model.exception.PaymentProcessingException;
import com.ms.paymentservice.repository.PaymentRepository;
import com.ms.paymentservice.service.PaymentService;
import com.ms.paymentservice.service.PaymentStrategy;
import com.ms.paymentservice.service.PaymentStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final ModelMapper modelMapper;

    @Override
    public PaymentDTO executePaymentProcessing(OrderDTO order) throws PaymentProcessingException {
        log.info("Processing payment for the order: {}", order);

        if (order == null || order.getPaymentMethod() == null) {
            log.error("Invalid order data: {}", order);
            throw new PaymentProcessingException("Invalid order data");
        }

        PaymentMethod paymentMethod;
        
        try {
            paymentMethod = PaymentMethod.fromString(order.getPaymentMethod().toString());
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment method: {}", order.getPaymentMethod());
            throw new InvalidPaymentMethodException("Invalid payment method: " + order.getPaymentMethod(), e);
        }

        // Obter a estratégia correta
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(paymentMethod);

        // Processar o pagamento usando a estratégia selecionada
        Payment payment = paymentStrategy.processPayment(order);

        // Simular processamento para métodos que não precisam de terceiros
        if (payment.getStatus() == null) {
            payment.setStatus(simulatePaymentProcessing() ? "APPROVED" : "FAILED");
        }

        // Salvar pagamento no repositório
        Payment savedPayment = paymentRepository.save(payment);

        // Criar DTO de resposta
        PaymentDTO paymentDTO = modelMapper.map(savedPayment, PaymentDTO.class);

        if (order.getItems() != null && !order.getItems().isEmpty()) {

            List<PaymentDTO.OrderItemDTO> items = mapOrderItemsToDTO(order);
            paymentDTO.setItems(items);

        }

        // Publicar evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                paymentDTO
        );
        log.info("Payment result sent successfully");

        return paymentDTO;
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.8;
    }

    private List<PaymentDTO.OrderItemDTO> mapOrderItemsToDTO(OrderDTO order) {
        return order.getItems().stream()
                .map(orderItem -> {
                    PaymentDTO.OrderItemDTO itemDTO = new PaymentDTO.OrderItemDTO();
                    itemDTO.setSku(orderItem.getSku());
                    itemDTO.setQuantity(orderItem.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());
    }

}
