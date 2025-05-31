package com.orderservice.controller;


import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<OrderResponse> createNewOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderServiceImpl.placeOrder(orderRequest));
    }


    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<OrderResponse> getOrderByOrderId(@PathVariable Long orderId) {
        return  ResponseEntity.ok(orderServiceImpl.getOrderById(orderId));
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<OrderResponse>> getOrderByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderServiceImpl.getOrdersByUserId(userId));
    }


}
