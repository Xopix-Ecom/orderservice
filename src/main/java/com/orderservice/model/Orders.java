package com.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Orders {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private String userId; // User ID from User Service

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Embedded // Embeds the Address fields directly into the 'orders' table
    private Address shippingAddress;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default // Ensures this list is initialized by default when using @Builder
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add order items and link them back to this order
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}

