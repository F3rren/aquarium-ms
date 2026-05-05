package it.f3rren.aquarium.aquariums_service.kafka;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import it.f3rren.aquarium.aquariums_service.kafka.event.AquariumEvent;
import it.f3rren.aquarium.aquariums_service.kafka.publisher.AquariumEventPublisher;

@ExtendWith(MockitoExtension.class)
class AquariumEventPublisherTest {

    @Mock
    private KafkaTemplate<String, AquariumEvent> kafkaTemplate;

    @InjectMocks
    private AquariumEventPublisher publisher;

    @Nested
    class PublishDeleted {

        @Test
        void sendsDeletedEventToKafka() {
            publisher.publishDeleted(1L);

            verify(kafkaTemplate).send("aquarium-events", "1", new AquariumEvent(1L, "DELETED"));
        }

        @Test
        void sendsWithCorrectAquariumId() {
            publisher.publishDeleted(42L);

            verify(kafkaTemplate).send("aquarium-events", "42", new AquariumEvent(42L, "DELETED"));
        }
    }

    @Nested
    class PublishCreated {

        @Test
        void sendsCreatedEventToKafka() {
            publisher.publishCreated(1L);

            verify(kafkaTemplate).send("aquarium-events", "1", new AquariumEvent(1L, "CREATED"));
        }

        @Test
        void sendsWithCorrectAquariumId() {
            publisher.publishCreated(99L);

            verify(kafkaTemplate).send("aquarium-events", "99", new AquariumEvent(99L, "CREATED"));
        }
    }
}
