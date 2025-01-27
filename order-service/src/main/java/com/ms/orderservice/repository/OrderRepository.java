package com.ms.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.orderservice.model.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}