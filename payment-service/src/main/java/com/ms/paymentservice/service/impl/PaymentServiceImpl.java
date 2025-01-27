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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

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

        try {

            if (order == null || order.getPaymentMethod() == null) {
                log.error("Invalid order data: {}", order);
                throw new PaymentProcessingException("Invalid order data");
            }

            PaymentMethod paymentMethod;

            try {
                paymentMethod = PaymentMethod.valueOf(order.getPaymentMethod().toUpperCase());
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

            // Enviar o evento de pagamento processado
            rabbitTemplate.convertAndSend("payment.exchange", "payment.routingkey", savedPayment);

            log.info("Payment result sent successfully");

            return paymentDTO;

        } catch (PaymentProcessingException e) {
            log.error("Error during payment processing: {}", e.getMessage());
            throw e; 
        }
    }
}
