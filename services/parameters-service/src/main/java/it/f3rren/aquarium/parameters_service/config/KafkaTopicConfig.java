package it.f3rren.aquarium.parameters_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic parameterMeasurementsTopic() {
        return TopicBuilder.name("parameter.measurements")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic parameterMeasurementsDltTopic() {
        return TopicBuilder.name("parameter.measurements.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
