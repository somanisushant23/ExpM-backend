package com.userservice.expmbff.service;

import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.TransactionDto;
import com.userservice.expmbff.entity.TransactionEntity;
import com.userservice.expmbff.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<SuccessResponse> createTransaction(TransactionDto transactionDto) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTitle(transactionDto.getTitle());
        transactionEntity.setDescription(transactionDto.getDescription());
        transactionEntity.setAmount(transactionDto.getAmount());
        transactionEntity.setTransactionDate(transactionDto.getTransactionDate());
        transactionEntity.setTransactionType(transactionDto.getTransactionType());
        transactionRepository.save(transactionEntity);
        SuccessResponse response = new SuccessResponse("Transaction created successfully");
        return ResponseEntity.ok(response);
    }
}
