package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.exception.EntityNotFoundException;
import com.mock2.shopping_app.model.request.RegistrationRequest;
import com.mock2.shopping_app.model.request.UserDTO;
import com.mock2.shopping_app.model.entity.Address;
import com.mock2.shopping_app.model.entity.Role;
import com.mock2.shopping_app.model.entity.User;
import com.mock2.shopping_app.model.enums.Gender;
import com.mock2.shopping_app.model.enums.RoleName;
import com.mock2.shopping_app.model.other.Page;
import com.mock2.shopping_app.repository.UserRepository;
import com.mock2.shopping_app.service.RoleService;
import com.mock2.shopping_app.service.UserService;
import com.mock2.shopping_app.util.Util;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = Logger.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        logger.info("Find all users with pagination and sorting");
        if (pageNo < 1) {
            throw new IllegalArgumentException("Page index must not be less than one");
        }
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(sortBy));
        org.springframework.data.domain.Page<User> pagedResult = userRepository.findAll(pageable);
        List<User> users = new ArrayList<>();
        if (pagedResult.hasContent()) {
            users = pagedResult.getContent();
        }
        Page<User> userPage = new Page<>();
        userPage.setContent(users);
        userPage.setCurrentPage(pagedResult.getNumber() + 1);
        userPage.setTotalItems(pagedResult.getTotalElements());
        userPage.setTotalPages(pagedResult.getTotalPages());
        return userPage;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        logger.info("Find user by id");
        return userRepository.findById(id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        logger.info("Creating user...");
        User newUser = new User();
        newUser.setEmail(registrationRequest.getEmail());
        newUser.setFirstName(registrationRequest.getFirstName());
        newUser.setLastName(registrationRequest.getLastName());
        newUser.setGender(Gender.valueOf(registrationRequest.getGender()));
        newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        newUser.setPhone(registrationRequest.getPhone());
        logger.info("Creating address...");
        Address address = new Address();
        address.setCity(registrationRequest.getAddress().getCity());
        address.setDistrict(registrationRequest.getAddress().getDistrict());
        address.setWard(registrationRequest.getAddress().getWard());
        address.setStreet(registrationRequest.getAddress().getStreet());
        address.setNumber(registrationRequest.getAddress().getNumber());
        newUser.setAddress(address);
        newUser.setEmailVerified(false);
        Boolean registrationAsAdmin = registrationRequest.getRegisterAsAdmin();
        newUser.setRoles(getRolesForNewUser(registrationAsAdmin));
        return newUser;
    }

    private Set<Role> getRolesForNewUser(Boolean isRegistrationAsAdmin) {
        Set<Role> roles = new HashSet<>();
        if (isRegistrationAsAdmin) {
            roles.add(roleService.findRoleByRoleName(RoleName.ROLE_ADMIN));
            return roles;
        }
        roles.add(roleService.findRoleByRoleName(RoleName.ROLE_USER));
        return roles;
    }

    @Override
    public User saveUser(User user) {
        logger.info("Save new user into database");
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        // Update only the non-null properties from userDTO
        logger.info("Partial update user information...");
        BeanUtils.copyProperties(userDTO, user, Util.getNullPropertyNames(userDTO));
        if (userDTO.getGender() != null && !userDTO.getGender().equals("")) {
            user.setGender(Gender.valueOf(userDTO.getGender()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        logger.info("Trying to delete user with id: " + id);
        userRepository.deleteById(id);
    }

}
