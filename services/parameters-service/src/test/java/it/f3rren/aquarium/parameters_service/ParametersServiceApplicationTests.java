package it.f3rren.aquarium.parameters_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class ParametersServiceApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("main() should delegate to SpringApplication.run with correct class argument")
    void mainShouldDelegateToSpringApplication() {
        try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
            mockedStatic.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                        .thenReturn(null);

            ParametersServiceApplication.main(new String[]{});

            mockedStatic.verify(() ->
                    SpringApplication.run(eq(ParametersServiceApplication.class), any(String[].class)));
        }
    }
}
