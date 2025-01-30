package com.ecommerce.notificationservice.business.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import com.ecommerce.notificationservice.domain.model.OrderDTO;
import com.ecommerce.notificationservice.domain.model.OrderItemDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private OrderDTO testOrder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "trackingUrlBase", "http://tracking.example.com/");
        
        testOrder = new OrderDTO();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@example.com");
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setStatus("CONFIRMED");
        
        OrderItemDTO item = new OrderItemDTO();
        item.setId(1L);
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("50.00"));
        
        testOrder.setItems(Arrays.asList(item));
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(IContext.class))).thenReturn("Test email content");
    }

    @Test
    void shouldSendOrderConfirmationEmailSuccessfully() throws MessagingException {
        // Act
        emailService.sendOrderConfirmationEmail(testOrder);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(templateEngine, times(1)).process(eq("order-confirmation"), any(IContext.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailSendingFails() throws MessagingException {
        // Arrange
        doThrow(new RuntimeException("Email sending failed"))
            .when(mailSender).send(any(MimeMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            emailService.sendOrderConfirmationEmail(testOrder));
    }

    @Test
    void shouldHandleNullOrderItems() {
        // Arrange
        testOrder.setItems(null);

        // Act
        emailService.sendOrderConfirmationEmail(testOrder);

        // Assert
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldExtractCustomerNameFromEmail() {
        // Arrange
        testOrder.setCustomerEmail("john.doe@example.com");

        // Act
        emailService.sendOrderConfirmationEmail(testOrder);

        // Assert
        verify(templateEngine, times(1)).process(eq("order-confirmation"), argThat(context -> 
            "john.doe".equals(((IContext) context).getVariable("customerName"))
        ));
    }

    @Test
    void shouldFormatCurrencyInBrazilianFormat() {
        // Act
        emailService.sendOrderConfirmationEmail(testOrder);

        // Assert
        verify(templateEngine, times(1)).process(eq("order-confirmation"), argThat(context -> 
            ((IContext) context).getVariable("orderTotal").toString().startsWith("R$")
        ));
    }

    @Test
    void shouldIncludeTrackingUrlInEmailContent() {
        // Act
        emailService.sendOrderConfirmationEmail(testOrder);

        // Assert
        verify(templateEngine, times(1)).process(eq("order-confirmation"), argThat(context -> 
            ((IContext) context).getVariable("trackingUrl")
                .equals("http://tracking.example.com/" + testOrder.getId())
        ));
    }
}