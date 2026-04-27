package it.f3rren.aquarium.aquariums_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic aquariumLifecycleTopic() {
        return TopicBuilder.name("aquarium.lifecycle")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aquariumLifecycleDltTopic() {
        return TopicBuilder.name("aquarium.lifecycle.DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
