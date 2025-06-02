package com.orderservice.dto.cart;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Data
public class CartDTO {
    private String id;
    private List<CartItemDTO> cartItems;
    private LastModifiedDate lastModifiedDate;
    private Long userId;
}
