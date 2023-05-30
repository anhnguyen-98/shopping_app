package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserDTO {

    @NotNull(message = "Username can not be blank")
    private String firstName;

    @NotNull(message = "Username can not be blank")
    private String lastName;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number")
    private String phone;

    @Setter
    @Pattern(regexp = "^(M|F)$", message = "Gender must be either M or F")
    private String gender;

    private AddressDTO address;
}
