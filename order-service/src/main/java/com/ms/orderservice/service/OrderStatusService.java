package com.ms.orderservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.model.entity.Order;
import com.ms.orderservice.model.entity.OrderStatus;
import com.ms.orderservice.model.entity.OrderStatusHistory;
import com.ms.orderservice.model.exception.InvalidStatusTransitionException;
import com.ms.orderservice.repository.OrderStatusHistoryRepository;

@Service
public class OrderStatusService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ModelMapper modelMapper; 

    public OrderStatusService(OrderStatusHistoryRepository orderStatusHistoryRepository, ModelMapper modelMapper) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.modelMapper = modelMapper;
    }


    // Apenas valida a transição de status e cria o histórico
    public void validateAndCreateStatusHistory(Order order, OrderStatus requestedStatus) {
        
        OrderStatus previousStatus = order.getStatus();

        if (!previousStatus.canTransitionTo(requestedStatus)) {
            throw new InvalidStatusTransitionException(previousStatus, requestedStatus);
        }

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(order, previousStatus, requestedStatus);
        orderStatusHistoryRepository.save(orderStatusHistory);
    }
   
    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Order order) {

        List<OrderStatusHistory> historyList = orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(order);
    
        return historyList.stream()
                .map(history -> modelMapper.map(history, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }
}
