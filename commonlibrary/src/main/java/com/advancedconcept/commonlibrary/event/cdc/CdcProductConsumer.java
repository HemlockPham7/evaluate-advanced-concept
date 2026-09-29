package com.advancedconcept.commonlibrary.event.cdc;

import com.advancedconcept.commonlibrary.event.pub.CacheInvalidationPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdcProductConsumer {

    private final CacheInvalidationPublisher cacheInvalidationPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(
            topics = "cdc.public.products",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(@Payload(required = false) String message) {

        if (message == null) {
            log.info("Receive Tombstone info (Delete payload in Debezium)");
            return;
        }

        try {
            log.info("Received CDC message for products table: {}", message);

            JsonNode rootNode = objectMapper.readTree(message);

            // Xử lý payload Debezium (Cấu trúc thông thường: payload.after / payload.before / payload.op)
            JsonNode payload = rootNode.has("payload") ? rootNode.get("payload") : rootNode;

            String op = payload.has("op") ? payload.get("op").asText() : ""; // c: create, u: update, d: delete
            Long productId = extractProductId(payload);

            switch (op) {
                case "c":
                    // Khi CREATE: Chỉ xóa cache Danh sách/Phân trang
                    cacheInvalidationPublisher.invalidateGroup("products", "list");
                    log.info("[CDC INVALIDATE - CREATE] Invalidated List Cache for new Product ID: {}", productId);
                    break;

                case "u":
                case "d":
                    // Khi UPDATE/DELETE: Xóa cả Detail ID tương ứng và Danh sách/Phân trang
                    if (productId != null) {
                        cacheInvalidationPublisher.invalidate("products", "detail", String.valueOf(productId));
                    }
                    cacheInvalidationPublisher.invalidateGroup("products", "list");
                    log.info("[CDC INVALIDATE - {}] Invalidated Detail ID: {} & List Cache", op.toUpperCase(), productId);
                    break;

                default:
                    log.debug("Ignored CDC operation: {}", op);
                    break;
            }

        } catch (Exception e) {
            log.error("Error processing CDC message: {}", message, e);
        }
    }

    private Long extractProductId(JsonNode payload) {
        // Trường hợp DELETE: dữ liệu nằm trong 'before'
        if (payload.has("before") && !payload.get("before").isNull()) {
            return payload.get("before").get("id").asLong();
        }
        // Trường hợp INSERT/UPDATE: dữ liệu nằm trong 'after'
        if (payload.has("after") && !payload.get("after").isNull()) {
            return payload.get("after").get("id").asLong();
        }
        return null;
    }
}
