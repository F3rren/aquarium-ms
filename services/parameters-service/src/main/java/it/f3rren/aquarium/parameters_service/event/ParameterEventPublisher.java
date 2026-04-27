package it.f3rren.aquarium.parameters_service.event;

import it.f3rren.aquarium.events.ParameterMeasuredEvent;
import it.f3rren.aquarium.parameters_service.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ParameterEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ParameterEventPublisher.class);
    private static final String TOPIC = "parameter.measurements";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ParameterEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishParameterMeasured(Parameter p) {
        ParameterMeasuredEvent event = new ParameterMeasuredEvent(
                String.valueOf(p.getAquariumId()),
                p.getTemperature(),
                p.getPh(),
                p.getSalinity() != null ? p.getSalinity().doubleValue() : null,
                p.getOrp() != null ? p.getOrp().doubleValue() : null,
                p.getMeasuredAt().toInstant(java.time.ZoneOffset.UTC)
        );
        kafkaTemplate.send(TOPIC, String.valueOf(p.getAquariumId()), event);
        log.info("Published ParameterMeasured for aquarium {}", p.getAquariumId());
    }
}
