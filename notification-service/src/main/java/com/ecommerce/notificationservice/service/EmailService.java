package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.model.OrderDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.tracking-url-base}")
    private String trackingUrlBase;

    public void sendOrderConfirmationEmail(OrderDTO order) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(order.getCustomerEmail());
            helper.setSubject("Pedido Confirmado - # " + order.getId());

            String content = buildEmailContent(order);
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Confirmation email sent successfully for order: {}", order.getId());

        } catch (Exception ex) {

            log.error("Error sending confirmation email for order {}: {}", order.getId(), ex.getMessage());
            throw new RuntimeException("Failed to send confirmation email", ex);

        }
    }

    private String buildEmailContent(OrderDTO order) {

        Context context = new Context(new Locale("pt", "BR"));


        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String formattedTotal = currencyFormatter.format(order.getTotalAmount());

        context.setVariable("customerName", extractCustomerName(order.getCustomerEmail()));
        context.setVariable("orderId", order.getId());
        context.setVariable("orderStatus", order.getStatus());
        context.setVariable("orderDate", order.getCreatedAt());
        context.setVariable("orderTotal", formattedTotal);
        context.setVariable("items", order.getItems() != null ? order.getItems() : new ArrayList<>());
        context.setVariable("trackingUrl", trackingUrlBase + order.getId());

        return templateEngine.process("order-confirmation", context);
    }

    private String extractCustomerName(String email) {
        return email.split("@")[0];
    }

}
