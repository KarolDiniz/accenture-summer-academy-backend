import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.OrderItemDTO;
import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;

import java.lang.reflect.Type;
import java.util.List;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Configuração personalizada de mapeamento para Order -> OrderDTO
        modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
            @Override
            protected void configure() {
                map().setPaymentMethod(source.getPaymentMethod());
                map().setItems(null); // Lidaremos com isso no post-converter
                map().setOrderStatusHistory(null); // Lidaremos com isso no post-converter
            }
        });

        // Configuração para mapear listas
        Type orderItemDTOListType = new TypeToken<List<OrderItemDTO>>() {}.getType();
        Type orderStatusHistoryDTOListType = new TypeToken<List<OrderStatusHistoryDTO>>() {}.getType();

        // Adicionando um PostConverter para lidar com listas complexas
        modelMapper.createTypeMap(Order.class, OrderDTO.class).setPostConverter(context -> {
            Order source = context.getSource();
            OrderDTO destination = context.getDestination();

            // Mapear items
            if (source.getItems() != null) {
                List<OrderItemDTO> itemDTOs = modelMapper.map(source.getItems(), orderItemDTOListType);
                destination.setItems(itemDTOs);
            }

            // Mapear orderStatusHistory
            if (source.getOrderStatusHistory() != null) {
                List<OrderStatusHistoryDTO> historyDTOs = modelMapper.map(source.getOrderStatusHistory(), orderStatusHistoryDTOListType);
                destination.setOrderStatusHistory(historyDTOs);
            }

            return destination;
        });

        return modelMapper;
    }
}
