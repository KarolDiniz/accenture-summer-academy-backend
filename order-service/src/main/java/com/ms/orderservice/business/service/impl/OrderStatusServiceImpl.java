package com.ms.orderservice.business.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.ms.orderservice.business.service.OrderStatusService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.entity.OrderStatusHistory;
import com.ms.orderservice.domain.exception.InvalidStatusTransitionException;
import com.ms.orderservice.domain.repository.OrderStatusHistoryRepository;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ModelMapper modelMapper; 
    
    public OrderStatusServiceImpl(OrderStatusHistoryRepository orderStatusHistoryRepository, ModelMapper modelMapper) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.modelMapper = modelMapper;
    }

    // Apenas valida a transição de status e cria o histórico
    @Override
    public void validateAndCreateStatusHistory(Order order, OrderStatus requestedStatus) {
        
        OrderStatus previousStatus = order.getStatus();
        
        if (previousStatus == requestedStatus) {
            return; 
        }

        if (!previousStatus.canTransitionTo(requestedStatus)) {
            throw new InvalidStatusTransitionException(previousStatus, requestedStatus);
        }

        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(order, previousStatus, requestedStatus);
        orderStatusHistoryRepository.save(orderStatusHistory);
    }
   
    @Override
    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Order order) {

        List<OrderStatusHistory> historyList = orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(order);
    
        return historyList.stream()
                .map(history -> modelMapper.map(history, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }
}
