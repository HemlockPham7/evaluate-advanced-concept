package com.conceptevaluation.mqservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class MqserviceApplication {

	static void main(String[] args) {
		SpringApplication.run(MqserviceApplication.class, args);
	}

	@Bean
	NewTopic greetings() {
		return TopicBuilder.name("greetings")
				.partitions(1)
				.replicas(1)
				.build();
	}

	@Bean
	ApplicationRunner runner(KafkaTemplate<String, String> template) {
		return args -> template.send("greetings", "Hello, Kafka");
	}

	@KafkaListener(topics = "greetings", groupId = "demo")
	public void listen(String message) {
		System.out.println("Received Message: " + message);
	}
}
