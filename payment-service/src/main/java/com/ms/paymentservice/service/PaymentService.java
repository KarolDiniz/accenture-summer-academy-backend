package com.ms.paymentservice.service;

<<<<<<< HEAD
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;
import com.ms.paymentservice.model.exception.PaymentProcessingException;
=======
import com.ms.paymentservice.model.OrderDTO;
import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
>>>>>>> origin/feature/stock-validations

public interface PaymentService {

<<<<<<< HEAD
    public abstract PaymentDTO executePaymentProcessing(OrderDTO order) throws PaymentProcessingException;
    
}
=======
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public void processPayment(OrderDTO order) {
        log.info("Processing payment for order: {}", order);

        // Criar registro de pagamento
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("CREDIT_CARD");

        // Simular processamento de pagamento
        boolean paymentSuccess = simulatePaymentProcessing();

        // Atualizar status do pagamento
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        // Criar DTO para envio
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(payment.getOrderId());
        paymentDTO.setStatus(payment.getStatus());

        // Adicionar os itens do pedido no PaymentDTO
        if (order.getItems() != null) {
            paymentDTO.setItems(
                    order.getItems().stream()
                            .map(item -> {
                                PaymentDTO.OrderItemDTO itemDTO = new PaymentDTO.OrderItemDTO();
                                itemDTO.setSku(item.getSku());
                                itemDTO.setQuantity(item.getQuantity());
                                return itemDTO;
                            })
                            .collect(Collectors.toList())
            );
        }

        log.info("Sending payment result: {}", paymentDTO);
        // Publicar evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                paymentDTO
        );
        log.info("Payment result sent successfully");
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.8;
    }
}
>>>>>>> origin/feature/stock-validations
