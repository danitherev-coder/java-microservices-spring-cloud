package com.microservice.students;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MicroserviceStudents {
	public static void main(String[] args) {
		SpringApplication.run(MicroserviceStudents.class, args);
	}
}
