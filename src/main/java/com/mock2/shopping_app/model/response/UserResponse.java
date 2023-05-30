package com.mock2.shopping_app.model.response;

import com.mock2.shopping_app.model.entity.Role;
import com.mock2.shopping_app.model.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Gender gender;
    private boolean isEmailVerified;
    private Set<RoleResponse> roles;
    private AddressResponse address;
}
