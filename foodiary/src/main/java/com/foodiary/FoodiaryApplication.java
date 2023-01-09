package com.foodiary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodiaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodiaryApplication.class, args);
	}

}
