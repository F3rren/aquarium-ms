package it.f3rren.aquarium.aquariums_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AquariumsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AquariumsServiceApplication.class, args);
	}

}
