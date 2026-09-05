package com.nomnom.ingredient_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class IngredientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngredientServiceApplication.class, args);
	}

}
