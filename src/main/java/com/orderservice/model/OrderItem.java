package com.orderservice.model;


import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders; // Parent Order

    @Column(name = "product_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String productId; // ID of the product from Product Service

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price; // Unit price at the time of order

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal; // quantity * price

    // Convenience method for setting up relationship from Order
    public void setOrder(Orders order) {
        this.orders = order;
    }
}
