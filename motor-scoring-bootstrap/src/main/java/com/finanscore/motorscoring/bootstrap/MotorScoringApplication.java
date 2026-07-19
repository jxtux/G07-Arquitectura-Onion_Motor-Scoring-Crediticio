package com.finanscore.motorscoring.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.finanscore.motorscoring")
public class MotorScoringApplication {
	public static void main(String[] args) {
		SpringApplication.run(MotorScoringApplication.class, args);
	}
}
