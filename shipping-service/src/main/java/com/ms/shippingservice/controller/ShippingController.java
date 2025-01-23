package com.ms.shippingservice.controller;

import com.ms.shippingservice.model.ShipmentTracking;
import com.ms.shippingservice.model.ShippingOrder;
import com.ms.shippingservice.service.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Tag(name = "Shipping Controller", description = "Endpoints for managing shipments")
public class ShippingController {
    private final ShippingService shippingService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get shipping information by order ID")
    public ResponseEntity<ShippingOrder> getShippingByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getShippingOrder(orderId));
    }

    @GetMapping("/tracking/{shippingOrderId}")
    @Operation(summary = "Get tracking information by shipping order ID")
    public ResponseEntity<List<ShipmentTracking>> getTracking(@PathVariable Long shippingOrderId) {
        return ResponseEntity.ok(shippingService.getShippingTracking(shippingOrderId));
    }

    @PostMapping("/{orderId}/status")
    @Operation(summary = "Update shipping status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam String location,
            @RequestParam String description) {
        shippingService.updateShippingStatus(orderId, status, location, description);
        return ResponseEntity.ok().build();
    }
}