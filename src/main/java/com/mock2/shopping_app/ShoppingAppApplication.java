package com.mock2.shopping_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@EnableJpaAuditing
@SpringBootApplication
public class ShoppingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingAppApplication.class, args);
	}
}
