package com.microservice.roles_y_permisos.service;

import com.microservice.roles_y_permisos.entity.Permission;
import com.microservice.roles_y_permisos.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createRole(Role role);

    Permission createPermission(Permission permission);

    List<Role> listRoles();

    Optional<Role> findByName(String name);

    Role getRoleByName(String name);

}
