package com.xypha.onlineBus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OnlineBusApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineBusApplication.class, args);
	}

}
