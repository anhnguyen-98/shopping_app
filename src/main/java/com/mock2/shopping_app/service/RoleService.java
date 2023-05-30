package com.mock2.shopping_app.service;

import com.mock2.shopping_app.model.entity.Role;
import com.mock2.shopping_app.model.enums.RoleName;

public interface RoleService {
    Role findRoleByRoleName(RoleName roleName);
}
