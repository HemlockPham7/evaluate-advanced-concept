package com.advancedconcept.commonlibrary.event.cdc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CdcProductConsumer {

    @KafkaListener(topics = "cdc.public.products", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message) {
        log.info("Received cdc message for products table: " + message);
    }
}
