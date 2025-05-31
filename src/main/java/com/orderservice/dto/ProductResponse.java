package com.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
}
