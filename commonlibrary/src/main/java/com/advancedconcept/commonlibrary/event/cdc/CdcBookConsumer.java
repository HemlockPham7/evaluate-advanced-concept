package com.advancedconcept.commonlibrary.event.cdc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CdcBookConsumer {

    @KafkaListener(topics = "cdc.public.books", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Received cdc message: " + message);
    }
}
