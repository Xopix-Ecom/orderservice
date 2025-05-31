package com.orderservice.service;


import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.dto.ProductResponse;
import com.orderservice.feign.ProductClient;
import com.orderservice.mapper.OrderMapper;
import com.orderservice.model.Orders;
import com.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, ProductResponse> productResponseRedisTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private static final Logger logger  = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static final String PRODUCT_CACHE_KEY_PREFIX = "product::";

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        ProductResponse productResponse =
                fetchProduct(orderRequest.getProductId());


        Orders orders = new Orders();
        orders.setProductId(orderRequest.getProductId());
        orders.setQuantity(orderRequest.getQuantity());
        orders.setUserId(orderRequest.getUserId());
        orderRepository.save(orders);

        return orderMapper.toOrderResponse(orders, productResponse);
    }


    @Override
    public OrderResponse getOrderById(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Product Not found"));
        ProductResponse response = fetchProduct(order.getProductId());
        return orderMapper.toOrderResponse(order, response);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findById(userId)
                .stream()
                .map(order -> {
                    ProductResponse productResponse = fetchProduct(order.getProductId());
                    return orderMapper.toOrderResponse(order, productResponse);
                }).collect(Collectors.toList());
    }

    private ProductResponse fetchProduct(Long productId) {

        try {
            ProductResponse product = productClient.getProductById(productId);
            productResponseRedisTemplate.opsForValue().set(PRODUCT_CACHE_KEY_PREFIX + productId, product);
            logger.info("Cached product info in Redis for id {}", productId);
            return product;
        } catch (Exception e) {
            logger.info("Get product from redis cache for id {}", productId);
            return productResponseRedisTemplate.opsForValue().get(PRODUCT_CACHE_KEY_PREFIX+productId);
        }
    }
}