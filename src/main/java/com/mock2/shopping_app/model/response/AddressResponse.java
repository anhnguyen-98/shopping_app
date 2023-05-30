package com.mock2.shopping_app.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long addressId;
    private String city;
    private String district;
    private String ward;
    private String street;
    private String number;
}
