package com.advancedconcept.commonlibrary.service;

public interface KafkaService {

    void sendMessage(String topic, String message);
}
