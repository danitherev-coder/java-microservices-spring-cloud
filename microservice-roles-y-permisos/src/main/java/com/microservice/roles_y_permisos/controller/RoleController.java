package com.microservice.roles_y_permisos.controller;

import com.microservice.roles_y_permisos.entity.Permission;
import com.microservice.roles_y_permisos.entity.Role;
import com.microservice.roles_y_permisos.service.RoleService;
import com.microservice.roles_y_permisos.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@RequestBody Role role){
        return roleService.createRole(role);
    }

    @PostMapping("/permission")
    @ResponseStatus(HttpStatus.CREATED)
    public Permission createPermission(@RequestBody Permission permission) {
        return roleService.createPermission(permission);
    }

    @GetMapping
    public List<Role> listRoles(){
        return roleService.listRoles();
    }

    @GetMapping("/search")
    public ResponseEntity<RoleDto> getRoleByName(@RequestParam String name) {
        Role role = roleService.getRoleByName(name);
        if (role == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new RoleDto(role.getId(), role.getName()));
    }
}
