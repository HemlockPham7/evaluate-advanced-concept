package com.advancedconcept.commonlibrary.controller;

import com.advancedconcept.commonlibrary.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    private final KafkaService kafkaService;

    public TestController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping("/log")
    public String generateLog() {
        log.info("Test ELK Integration Success - Current time: {}", LocalDateTime.now());
        log.warn("This is a warning log test");
        return "Log generated!";
    }

    @PostMapping("/kafka-health")
    public void mqHealthCheck(@RequestBody String message) {
        kafkaService.sendMessage("health-check", message);
    }
}