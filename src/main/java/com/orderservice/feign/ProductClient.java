package com.orderservice.feign;


import com.orderservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "product-service", url = "${product.service.url}", fallback = ProductClientFallback.class)
public interface ProductClient {

    @RequestMapping(value = "/api/product/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);
}
