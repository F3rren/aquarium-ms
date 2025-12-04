package it.f3rren.aquarium.gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Autowired
    private RouteDefinitionLocator locator;

    @Bean
    public List<GroupedOpenApi> apis() {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        
        definitions.stream()
            .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
            .forEach(routeDefinition -> {
                String name = routeDefinition.getId();
                groups.add(GroupedOpenApi.builder()
                    .group(name)
                    .pathsToMatch("/" + name.replace("-service", "") + "/**")
                    .build());
            });
        
        return groups;
    }
}
