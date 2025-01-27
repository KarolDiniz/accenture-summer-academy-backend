package com.ms.orderservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderItem;
import com.ms.orderservice.model.OrderStatusHistory;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.OrderItemDTO;
import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;

public class OrderMapper {
    private final ModelMapper modelMapper;

    public OrderMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public OrderDTO toDTO(Order order) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
            .map(item -> modelMapper.map(item, OrderItemDTO.class))
            .collect(Collectors.toList());
        
        List<OrderStatusHistoryDTO> statusHistoryDTOs = order.getOrderStatusHistory().stream()
            .map(history -> modelMapper.map(history, OrderStatusHistoryDTO.class))
            .collect(Collectors.toList());
        
        orderDTO.setItems(itemDTOs);
        orderDTO.setOrderStatusHistory(statusHistoryDTOs);
        
        return orderDTO;
    }

    public Order toEntity(OrderDTO orderDTO) {
        Order order = modelMapper.map(orderDTO, Order.class);

        List<OrderItem> items = orderDTO.getItems().stream()
            .map(itemDTO -> modelMapper.map(itemDTO, OrderItem.class))
            .collect(Collectors.toList());

        List<OrderStatusHistory> statusHistory = orderDTO.getOrderStatusHistory().stream()
            .map(historyDTO -> modelMapper.map(historyDTO, OrderStatusHistory.class))
            .collect(Collectors.toList());

        order.setItems(items);
        order.setOrderStatusHistory(statusHistory);

        return order;
    }
}
