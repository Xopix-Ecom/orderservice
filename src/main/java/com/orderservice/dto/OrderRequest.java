package com.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotBlank(message = "Cart ID is required")
    private String cartId;

    @NotNull(message = "Shipping address is required")
    @Valid // Ensure nested AddressDto is also validated
    private AddressDTO shippingAddress;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
}
