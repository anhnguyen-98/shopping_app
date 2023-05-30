package com.mock2.shopping_app.service.impl;

import com.mock2.shopping_app.model.entity.Role;
import com.mock2.shopping_app.model.enums.RoleName;
import com.mock2.shopping_app.repository.RoleRepository;
import com.mock2.shopping_app.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findRoleByRoleName(RoleName roleName) {
        return roleRepository.findByRole(roleName);
    }
}
