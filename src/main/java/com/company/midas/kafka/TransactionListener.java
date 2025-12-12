
package com.company.midas.kafka;

import com.company.midas.dto.TransactionMessage;
import com.company.midas.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final TransactionService transactionService;

    public TransactionListener(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(topics = "tx-topic", groupId = "midas-core-group")
    public void onMessage(String payload) {
        try {
            // We receive raw JSON string; map to DTO inside service (or you can parse here)
            // For simplicity, use your ObjectMapper inside the service.process method which already expects a DTO.
            // If your TransactionService.process expects TransactionMessage, you can parse here instead:
            // TransactionMessage msg = objectMapper.readValue(payload, TransactionMessage.class);
            // transactionService.process(msg);

            // If your existing TransactionService.process(Message) expects TransactionMessage,
            // modify it accordingly. Here we assume TransactionService has a method processFromJson(String).
            log.info("Received raw message: {}", payload);
            transactionService.processFromJson(payload);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
            // do not throw — swallowing here prevents consumer thread from crashing
        }
    }
}
