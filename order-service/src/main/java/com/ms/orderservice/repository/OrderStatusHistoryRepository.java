package com.ms.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.orderservice.model.entity.Order;
import com.ms.orderservice.model.entity.OrderStatusHistory;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderOrderByChangeDateDesc(Order order);

}
