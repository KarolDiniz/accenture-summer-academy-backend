package com.ms.paymentservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ms.paymentservice.domain.model.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {}