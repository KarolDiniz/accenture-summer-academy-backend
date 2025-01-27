package com.ms.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.paymentservice.model.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {}