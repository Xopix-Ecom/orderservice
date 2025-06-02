package com.orderservice.service;


import com.orderservice.client.CartServiceClient;
import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.ProductResponse;
import com.orderservice.dto.cart.CartDTO;
import com.orderservice.exception.CartEmptyException;
import com.orderservice.exception.CartNotFoundException;
import com.orderservice.exception.OrderNotFoundException;
import com.orderservice.model.Address;
import com.orderservice.model.OrderItem;
import com.orderservice.model.OrderStatus;
import com.orderservice.model.Orders;
import com.orderservice.repository.OrderRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private RedisTemplate<String, ProductResponse> productResponseRedisTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private CartDTO cartDTO;

//    private static final String PRODUCT_CACHE_KEY_PREFIX = "product::";


    /**
     * Creates a new order from a shopping cart.
     * In this basic phase, it saves the order.
     * In later phases, this will trigger a Saga for distributed transactions.
     *
     * @param orderRequest The order creation request.
     * @param userId The ID of the authenticated user placing the order.
     * @return The created Order entity.
     * @throws CartNotFoundException if the cart does not exist.
     * @throws CartEmptyException if the cart is empty.
     */
    @Override
    public Orders createOrder(OrderRequest orderRequest, String userId) throws CartNotFoundException, CartEmptyException {

        try {
            cartDTO = cartServiceClient.getCartById(orderRequest.getCartId());
        } catch (FeignException.NotFound e) {
            log.warn("Cart with ID {} not found for order creation by user {}", orderRequest.getCartId(), userId);
            throw new CartNotFoundException("Cart with ID " + orderRequest.getCartId() + " not found.");
        }

        if(cartDTO.getCartItems() == null || cartDTO.getCartItems().isEmpty()) {
            log.warn("Attempted to create order from empty cart {}.", orderRequest.getCartId());
            throw new CartEmptyException("Cannot create an order from an empty cart.");
        }

        BigDecimal totalAmount = cartDTO.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Address shippingAddress = Address.builder()
                .street(orderRequest.getShippingAddress().getStreet())
                .city(orderRequest.getShippingAddress().getCity())
                .state(orderRequest.getShippingAddress().getState())
                .zipCode(orderRequest.getShippingAddress().getZipCode())
                .country(orderRequest.getShippingAddress().getCountry())
                .build();

        Orders newOrder = Orders.builder()
                .userId(userId)
                .status(OrderStatus.PENDING) // Initial status
                .totalAmount(totalAmount)
                .shippingAddress(shippingAddress)
                .build();

        cartDTO.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .subtotal(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            newOrder.addOrderItem(orderItem); // Adds to list and sets parent order
        });



        Orders savedOrder = orderRepository.save(newOrder);
        log.info("Order {} created for user {} from cart {}. Status: {}",
                savedOrder.getId(), userId, orderRequest.getCartId(), savedOrder.getStatus());

        // In a later phase, after successful order creation and perhaps inventory reservation,
        // you we will clear the cart: cartServiceClient.clearCart(request.getCartId());

        return savedOrder;
    }


    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The ID of the order.
     * @param userId The ID of the authenticated user (for authorization check).
     * @return The Order entity.
     * @throws OrderNotFoundException if the order is not found.
     */
    @Override
    public Orders getOrderById(String orderId, String userId) throws OrderNotFoundException {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        // Basic authorization: user can only view their own orders unless they are an admin
        if (!order.getUserId().equals(userId)) {
            log.warn("User {} attempted to access order {} belonging to user {}", userId, orderId, order.getUserId());
            // In a real app, this would be a Forbidden (403) exception or handled by @PreAuthorize
            // For now, re-throwing Not Found might be a way to hide existence from unauthorized users.
            throw new OrderNotFoundException("Order with ID " + orderId + " not found.");
        }
        return order;
    }


    /**
     * Retrieves all orders for a specific user with pagination and sorting.
     *
     * @param userId The ID of the user.
     * @param page   Page number (0-indexed).
     * @param size   Number of orders per page.
     * @param sortBy Field to sort by.
     * @param sortDir Sort direction (asc or desc).
     * @return A page of Order entities.
     */
    @Override
    public Page<Orders> getOrdersByUserId(String userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("Fetching orders for user {} (page: {}, size: {}, sort: {})", userId, page, size, sortBy);
        return orderRepository.findByUserId(userId, pageable);
    }

//    private ProductResponse fetchProduct(Long productId) {
//
//        try {
//            ProductResponse product = productClient.getProductById(productId);
//            productResponseRedisTemplate.opsForValue().set(PRODUCT_CACHE_KEY_PREFIX + productId, product);
//            logger.info("Cached product info in Redis for id {}", productId);
//            return product;
//        } catch (Exception e) {
//            logger.info("Get product from redis cache for id {}", productId);
//            return productResponseRedisTemplate.opsForValue().get(PRODUCT_CACHE_KEY_PREFIX+productId);
//        }
//    }
}