package com.company.midas.service;

import com.company.midas.dto.TransactionMessage;
import com.company.midas.model.OutboxEvent;
import com.company.midas.model.Transaction;
import com.company.midas.repository.OutboxRepository;
import com.company.midas.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    private final TransactionRepository txRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransactionService(TransactionRepository txRepo, OutboxRepository outboxRepo) {
        this.txRepo = txRepo;
        this.outboxRepo = outboxRepo;
    }

    @Transactional
    public void process(TransactionMessage msg) throws Exception {
        // basic validation
        if (msg.getAmount() == null || msg.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        // dedupe; if present skip silently
        if (txRepo.findByExternalTxId(msg.getExternalTxId()).isPresent()) {
            return;
        }

        Transaction tx = new Transaction();
        tx.setExternalTxId(msg.getExternalTxId());
        tx.setAmount(msg.getAmount());
        tx.setCurrency(msg.getCurrency());
        tx.setStatus("RECEIVED");
        tx = txRepo.save(tx);

        OutboxEvent outbox = new OutboxEvent();
        outbox.setAggregateId(tx.getId());
        outbox.setType("TRANSACTION_CREATED");
        outbox.setPayload(objectMapper.writeValueAsString(msg));
        outboxRepo.save(outbox);
    }

 public void processFromJson(String json) throws Exception {
        TransactionMessage msg = objectMapper.readValue(json, TransactionMessage.class);
        process(msg);
    }
}