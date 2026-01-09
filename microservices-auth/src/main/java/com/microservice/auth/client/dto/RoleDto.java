package com.microservice.auth.client.dto;

import java.util.List;

public class RoleDto {
    private Long id;
    private String name;
    private List<PermissionDto> permissions;

    public RoleDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<PermissionDto> getPermissions() { return permissions; }
    public void setPermissions(List<PermissionDto> permissions) { this.permissions = permissions; }
}
