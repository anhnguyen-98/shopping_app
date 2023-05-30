package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.request.UserDTO;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.other.Page;

import java.util.Optional;

public interface UserService {

    Page<User> findAll(Integer pageNo, Integer pageSize, String sortBy);

    Optional<User> findUserById(Long id);

    Boolean existsByEmail(String email);

    User createUser(RegistrationRequest registrationRequest);

    User saveUser(User user);

    User updateUser(Long userId, UserDTO user);

    void deleteUser(Long Id);
}
