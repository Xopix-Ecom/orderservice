package com.orderservice.feign;

import com.orderservice.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductClientFallback implements ProductClient {

    @Autowired
    private RedisTemplate<String, ProductResponse> redisTemplate;

    @Override
    public ProductResponse getProductById(Long productId) {
        String key = "product::" + productId;
        ProductResponse cacheResponse = redisTemplate.opsForValue().get(key);
        if(cacheResponse == null) {
            return new ProductResponse(productId, "Unavailable", "Fallback Product", BigDecimal.ZERO, "");
        }

        System.out.println("Returning product from Redis cache");
        return cacheResponse;
    }
}
