package com.ms.orderservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatusHistory;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderOrderByChangeDateDesc(Order order);

}
