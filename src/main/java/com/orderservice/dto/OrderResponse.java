package com.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long productId;
    private Integer quantity;
    private ProductResponse productResponse;
    private Long userId;
}
