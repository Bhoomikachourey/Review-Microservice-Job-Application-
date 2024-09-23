package com.pack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReviewMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewMsApplication.class, args);
	}

}
