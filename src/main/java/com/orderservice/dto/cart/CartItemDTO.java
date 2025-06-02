package com.orderservice.dto.cart;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
}
