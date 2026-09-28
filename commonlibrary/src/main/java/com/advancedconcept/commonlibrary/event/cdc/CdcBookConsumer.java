package com.advancedconcept.commonlibrary.event.cdc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CdcBookConsumer {

    @KafkaListener(
            topics = "cdc.public.books",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Payload(required = false) String message) {

        if (message == null) {
            log.info("Received tombstone message");
            return;
        }

        log.info("Received cdc message: {}", message);
    }
}
