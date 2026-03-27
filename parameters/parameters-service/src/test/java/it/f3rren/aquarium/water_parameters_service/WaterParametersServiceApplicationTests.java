package it.f3rren.aquarium.water_parameters_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import it.f3rren.aquarium.parameters_service.ParametersServiceApplication;

@SpringBootTest(classes = ParametersServiceApplication.class)
@org.springframework.test.context.TestPropertySource(locations = "classpath:application.properties")
class WaterParametersServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
