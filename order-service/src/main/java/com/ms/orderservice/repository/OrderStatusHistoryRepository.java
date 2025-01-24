package com.ms.orderservice.repository;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderOrderByChangeDateDesc(Order order);

}
