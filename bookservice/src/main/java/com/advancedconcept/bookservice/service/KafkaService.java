package com.advancedconcept.bookservice.service;

public interface KafkaService {

    void sendMessage(String topic, String message);
}
