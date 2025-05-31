package com.orderservice.mapper;

import com.orderservice.dto.OrderResponse;
import com.orderservice.dto.ProductResponse;
import com.orderservice.model.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(source = "productResponse.productId", target = "productId")
    @Mapping(source = "productResponse", target = "productResponse")
    OrderResponse toOrderResponse(Orders orders, ProductResponse productResponse);
}

