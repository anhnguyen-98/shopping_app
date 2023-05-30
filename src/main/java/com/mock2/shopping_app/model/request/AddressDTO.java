package com.mock2.shopping_app.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private String city;
    private String district;
    private String ward;
    private String street;
    private String number;
}
