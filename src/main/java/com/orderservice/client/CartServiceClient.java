package com.orderservice.client;

import com.orderservice.dto.cart.CartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "cart-service", url = "${xopix.services.cart.url}")
public interface CartServiceClient {


    @RequestMapping(value = "/api/carts/{cartId}")
    CartDTO getCartById(@PathVariable String cartId);
}
