package com.example.intelligence;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableRabbit
@EnableDiscoveryClient
public class IntelligenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntelligenceApplication.class, args);
	}

}
