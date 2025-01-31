package com.ms.orderservice.presentation.controller;

import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.business.service.OrderMapper;
import com.ms.orderservice.business.service.OrderService;
import com.ms.orderservice.business.service.OrderStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Endpoints for managing orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;
    private final OrderMapper orderMapper;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "Update an order status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> ordersDTO = orderService.getAllOrders();
        return ResponseEntity.ok(ordersDTO);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order by ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get order status history")
    public ResponseEntity<List<OrderStatusHistoryDTO>> getOrderStatusHistory(@PathVariable Long id) {
        Order order = orderMapper.toEntity(orderService.getOrder(id));
        List<OrderStatusHistoryDTO> history = orderStatusService.getOrderStatusHistory(order);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody Order updatedOrder) {
        OrderDTO updatedOrderDTO = orderService.updateOrder(id, updatedOrder);
        return ResponseEntity.ok(updatedOrderDTO);
    }
}
