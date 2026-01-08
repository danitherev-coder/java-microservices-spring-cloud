package com.microservice.roles_y_permisos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceRolesYPermisosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceRolesYPermisosApplication.class, args);
	}

}
