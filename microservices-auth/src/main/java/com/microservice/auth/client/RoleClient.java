package com.microservice.auth.client;

import com.microservice.auth.client.dto.RoleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-roles-y-permisos")
public interface RoleClient {

    @GetMapping("/api/v1/roles/search")
    RoleDto getRoleByName(@RequestParam String name);
}
