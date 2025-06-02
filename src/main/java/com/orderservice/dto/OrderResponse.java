package com.orderservice.dto;


import com.orderservice.model.OrderStatus;
import com.orderservice.model.Orders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String id;
    private String userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private AddressDTO shippingAddress; // Use AddressDto for response
    private List<OrderItemResponse> orderItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse fromEntity(Orders order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(AddressDTO.builder() // Map Address entity to AddressDto
                        .street(order.getShippingAddress().getStreet())
                        .city(order.getShippingAddress().getCity())
                        .state(order.getShippingAddress().getState())
                        .zipCode(order.getShippingAddress().getZipCode())
                        .country(order.getShippingAddress().getCountry())
                        .build())
                .orderItems(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
