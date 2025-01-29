package com.ms.paymentservice.business.service.impl;

import com.ms.paymentservice.business.service.PaymentService;
import com.ms.paymentservice.business.service.PaymentStrategy;
import com.ms.paymentservice.business.service.PaymentStrategyFactory;
import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.dto.PaymentDTO;
import com.ms.paymentservice.domain.model.entity.Payment;
import com.ms.paymentservice.domain.model.entity.PaymentMethod;
import com.ms.paymentservice.domain.model.exception.InvalidPaymentMethodException;
import com.ms.paymentservice.domain.model.exception.PaymentProcessingException;
import com.ms.paymentservice.domain.repository.PaymentRepository;
import com.ms.paymentservice.infrastructure.producer.PaymentEventPublisher;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final PaymentEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

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

        Payment savedPayment = paymentRepository.save(payment);

        PaymentDTO paymentDTO = modelMapper.map(savedPayment, PaymentDTO.class);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<PaymentDTO.OrderItemDTO> items = mapOrderItemsToDTO(order);
            paymentDTO.setItems(items);
        }

        // Publicar evento de pagamento processado
        eventPublisher.publishPaymentProcessed(paymentDTO);
        log.info("Payment result sent successfully");

        return paymentDTO;
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
