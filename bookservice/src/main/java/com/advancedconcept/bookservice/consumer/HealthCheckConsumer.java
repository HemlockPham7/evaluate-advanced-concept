package com.advancedconcept.bookservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HealthCheckConsumer {

    @KafkaListener(topics = "health-check", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Received message: " + message);
    }
}
