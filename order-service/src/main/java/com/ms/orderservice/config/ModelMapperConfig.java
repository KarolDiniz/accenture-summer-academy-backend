package com.ms.orderservice.config; 

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true);
        return modelMapper;
    }
}
// @Bean
//     public ModelMapper modelMapper() {
//         ModelMapper modelMapper = new ModelMapper();

//         // Configuração para mapear Order -> OrderDTO
//         modelMapper.typeMap(Order.class, OrderDTO.class).addMappings(mapper -> {
//             mapper.map(Order::getCustomerEmail, OrderDTO::setCustomerEmail);
//             mapper.map(Order::getPaymentMethod, OrderDTO::setPaymentMethod);
//         });

//         // Configuração para mapear OrderItem -> OrderItemDTO
//         modelMapper.typeMap(OrderItem.class, OrderItemDTO.class).addMappings(mapper -> {
//             mapper.map(OrderItem::getProductId, OrderItemDTO::setProductId);
//             mapper.map(OrderItem::getPrice, OrderItemDTO::setPrice);
//         });

//         // Configuração para mapear OrderStatusHistory -> OrderStatusHistoryDTO
//         modelMapper.typeMap(OrderStatusHistory.class, OrderStatusHistoryDTO.class).addMappings(mapper -> {
//             mapper.map(OrderStatusHistory::getPreviousStatus, OrderStatusHistoryDTO::setPreviousStatus);
//             mapper.map(OrderStatusHistory::getCurrentStatus, OrderStatusHistoryDTO::setCurrentStatus);
//             mapper.map(OrderStatusHistory::getChangeDate, OrderStatusHistoryDTO::setChangeDate);
//         });

//         // Configuração geral
//         modelMapper.getConfiguration()
//                 .setSkipNullEnabled(true) 
//                 .setFieldMatchingEnabled(true); 

//         return modelMapper;
//     }

