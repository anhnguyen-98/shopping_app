package com.mock2.shopping_app.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotNull(message = "Email can not be blank")
    @Email
    private String email;

    @NotNull(message = "Username can not be blank")
    private String firstName;

    @NotNull(message = "Username can not be blank")
    private String lastName;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number")
    private String phone;

    @Pattern(regexp = "^(M|F)$", message = "Gender must be either M or F")
    private String gender;

    @NotNull(message = "Login password can not be blank")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$", message = "Password must contain at least 6 characters, including uppercase, lowercase and number")
    private String password;

    @NotNull(message = "Specify whether the user has to be registered as an admin or not")
    private Boolean registerAsAdmin;

    private AddressDTO address;
}
