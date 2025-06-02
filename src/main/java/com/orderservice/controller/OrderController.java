package com.orderservice.controller;


import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.model.Orders;
import com.orderservice.service.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
                                                     Authentication authentication) {
        // In a real application, authentication.getName() would map to the internal userId
        // or a custom principal object from Auth0 JWT validation.
        // For local testing with in-memory users, authentication.getName() is the username ("user1").
        // We'll use this as the userId for now.
        String userId = authentication.getName();
        log.info("Received order creation request for user {} from cart {}", userId, request.getCartId());
        Orders newOrder = orderServiceImpl.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(newOrder));
    }


    /**
     * Get order details by ID.
     * Requires authentication. User can only view their own orders unless they are ADMIN.
     */
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and (authentication.name == @orderServiceImpl.getOrderById(#orderId, authentication.name).userId or hasRole('ADMIN'))")
    public ResponseEntity<OrderResponse> getOrderByOrderId(@PathVariable String  orderId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("Fetching order {} for user {}", orderId, userId);
        Orders order = orderServiceImpl.getOrderById(orderId, userId); // Service handles ownership check
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    /**
     * Get all orders for a specific user.
     * Requires authentication. User can only view their own orders unless they are ADMIN.
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated() and (#userId == authentication.name or hasRole('ADMIN'))")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        // Enforce: authenticated user can only request their own userId unless ADMIN
        if (!userId.equals(authentication.getName()) && !authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // This case should be caught by @PreAuthorize, but a defensive check is good.
            log.warn("User {} attempted to fetch orders for user {}", authentication.getName(), userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        log.info("Fetching orders for user {} (page: {}, size: {}, sortBy:{})", userId, page, size, sortBy);
        Page<Orders> ordersPage = orderServiceImpl.getOrdersByUserId(userId, page, size, sortBy, sortDir);
        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderResponses);
    }


}
