package com.ms.paymentservice.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.ms.paymentservice.service.PaymentService;


public class PaymentController {


    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
 
}
