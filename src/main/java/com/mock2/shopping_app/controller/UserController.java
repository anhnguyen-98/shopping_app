package com.mock2.shopping_app.controller;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.request.UserDTO;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.model.response.ApiResponse;
import com.mock2.shopping_app.model.response.UserResponse;
import com.mock2.shopping_app.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.path}")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> findAll(
            @RequestParam(defaultValue = "1", name = "pageNo") Integer pageNo,
            @RequestParam(defaultValue = "5", name = "pageSize") Integer pageSize,
            @RequestParam(defaultValue = "id", name = "sortBy") String sortBy
    ) {
        Page<User> userPage = userService.findAll(pageNo, pageSize, sortBy);
        List<UserResponse> userResponseList = userPage.getContent().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
        Page<UserResponse> userResponsePage = new Page<>();
        userResponsePage.setContent(userResponseList);
        userResponsePage.setTotalPages(userPage.getTotalPages());
        userResponsePage.setCurrentPage(userPage.getCurrentPage());
        userResponsePage.setTotalItems(userPage.getTotalItems());
        return ResponseEntity.ok(userResponsePage);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> ResponseEntity.ok(modelMapper.map(user, UserResponse.class)))
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PatchMapping("/user/{id}")
    public ResponseEntity<UserResponse> updateUserInfo(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(modelMapper.map(userService.updateUser(id, userDTO), UserResponse.class));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully delete user with id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ApiResponse(false, "Could not delete user with id: " + id));
        }
    }
}
