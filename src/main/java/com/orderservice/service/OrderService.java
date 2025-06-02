package com.orderservice.service;


import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.model.Orders;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Orders createOrder(OrderRequest orderRequest, String userId);
    Orders getOrderById(String orderId, String userId);
    Page<Orders> getOrdersByUserId(String userId, int page, int size, String sortBy, String sortDir);
}

