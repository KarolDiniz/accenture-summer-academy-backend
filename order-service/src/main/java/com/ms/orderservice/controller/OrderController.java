package com.ms.orderservice.controller;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.service.OrderService;
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

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an order status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get order status history")
    public ResponseEntity<List<OrderStatusHistoryDTO>> getOrderStatusHistory(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderStatusHistory(id));
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order by ID")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }


}
