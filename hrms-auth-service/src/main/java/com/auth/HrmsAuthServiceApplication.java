package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.auth.external")
public class HrmsAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HrmsAuthServiceApplication.class, args);
	}

}
