package com.advancedconcept.commonlibrary.event.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventConsumer {

    @KafkaListener(topics = "health-check", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Received message: " + message);
    }
}
