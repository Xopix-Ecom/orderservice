package com.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDTO {
    @NotBlank(message = "Street is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    private String state;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    @NotBlank(message = "Country is required")
    private String country;
}
