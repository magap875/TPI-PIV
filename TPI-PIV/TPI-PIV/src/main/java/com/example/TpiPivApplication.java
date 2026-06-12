package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.config.jwt.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class TpiPivApplication {

	public static void main(String[] args) {
		SpringApplication.run(TpiPivApplication.class, args);
	}

}
