# evaluate-advanced-concept
implementation of batch processing to upload specific process including completed and canceled metadata to s3 storage, also develop a basic system to get the necessary metadata to update eleastic search table with debezium and kafka, integrating kibana for monitoring the log and grafana to monitor the system will be needed


## Product Structure

```
evaluate-advanced-concept/
│
├── commonlibrary/
│   ├── src/main/resouces/
│   │   ├── application.yml
│   │   └── logback-spring.xml
│   └── pom.xml
│
├── deployment/
│   ├── elk/
│   │   ├── pipeline/
│   │   │   └── logstash.conf
│   │   ├── elasticsearch.yaml
│   │   ├── kibana.yaml
│   │   └── logstash.yaml
│   ├── collector/
│   │   └── otel-collector-config.yaml
│   ├── prometheus/
│   │   └── prometheus.yaml
│   ├── tempo/
│   │   └── tempo.yaml
│   ├── grafana/
│   │   └── grafana-datasources.yaml
│   └── opentelemetry/
│       ├── opentelemetry-javaagent.jar
│       └── opentelemetry-config.properties
│
├── benmark/
├── http/
├── postgres_data/
├── Dockerfile
├── .dockerignore
└── docker-compose.yml

```

```
Spring Boot
    │
    │ OTLP
    ▼
otel-collector:4317
    │
    │ Prometheus exporter
    ▼
otel-collector:8889
    │
    │ scrape
    ▼
Prometheus:9090

```