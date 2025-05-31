package com.orderservice.config;

import com.orderservice.dto.OrderResponse;
import com.orderservice.dto.ProductResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, ProductResponse> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ProductResponse> productResponseRedisTemplate =
                new RedisTemplate<>();

        productResponseRedisTemplate.setConnectionFactory(redisConnectionFactory);
        productResponseRedisTemplate.setKeySerializer(new StringRedisSerializer());
        productResponseRedisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(ProductResponse.class));

        return productResponseRedisTemplate;
    }
}
