package com.microservice.roles_y_permisos.service.impl;

import com.microservice.roles_y_permisos.entity.Permission;
import com.microservice.roles_y_permisos.entity.Role;
import com.microservice.roles_y_permisos.repository.PermissionRepository;
import com.microservice.roles_y_permisos.repository.RoleRepository;
import com.microservice.roles_y_permisos.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }
}
