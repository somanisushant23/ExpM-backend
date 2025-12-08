package com.userservice.expmbff.service;

import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.TransactionDto;
import com.userservice.expmbff.dto.TransactionResponseDto;
import com.userservice.expmbff.entity.TransactionEntity;
import com.userservice.expmbff.entity.UserEntity;
import com.userservice.expmbff.exceptions.IncorrectDataException;
import com.userservice.expmbff.repository.TransactionRepository;
import com.userservice.expmbff.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<List<TransactionResponseDto>> createTransactions(List<TransactionDto> transactionDtos, String authenticatedEmail) throws IncorrectDataException {
        // Find authenticated user
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));

        logger.info("Creating transaction {}  for user {}", transactionDtos, authenticatedEmail);
        List<TransactionResponseDto> responseDtos = new ArrayList<>();

        for (TransactionDto transactionDto : transactionDtos) {
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setTitle(transactionDto.getTitle());
            transactionEntity.setDescription(transactionDto.getDescription());
            transactionEntity.setAmount(transactionDto.getAmount());
            transactionEntity.setTransactionDate(transactionDto.getTransactionDate());
            transactionEntity.setTransactionType(transactionDto.getTransactionType());
            transactionEntity.setUser(user);
            transactionEntity.setCategory(transactionDto.getCategory());
            transactionEntity.setClientId(UUID.fromString(transactionDto.getClientId()));
            if (transactionDto.getCreatedOn() != null) {
                transactionEntity.setCreatedOn(transactionDto.getCreatedOn());
            }
            if(transactionDto.getUpdatedOn() != null) {
                transactionEntity.setUpdatedOn(transactionDto.getUpdatedOn());
            }
            TransactionEntity savedEntity = transactionRepository.save(transactionEntity);

            TransactionResponseDto responseDto = new TransactionResponseDto();
            responseDto.setId(transactionEntity.getId());
            responseDto.setTitle(transactionEntity.getTitle());
            responseDto.setCategory(transactionEntity.getCategory());
            responseDto.setDescription(transactionEntity.getDescription());
            responseDto.setAmount(transactionEntity.getAmount());
            responseDto.setTransactionDate(transactionEntity.getTransactionDate());
            responseDto.setTransactionType(transactionEntity.getTransactionType().name());
            responseDto.setCreatedOn(transactionEntity.getCreatedOn());
            responseDto.setUpdatedOn(transactionEntity.getUpdatedOn());
            responseDto.setClientId(savedEntity.getClientId().toString());

            responseDtos.add(responseDto);
        }

        return ResponseEntity.ok(responseDtos);
    }

    @Transactional
    public ResponseEntity<SuccessResponse> updateTransaction(Long id, TransactionDto patchDto, String authenticatedEmail) throws IncorrectDataException {
        // Locate transaction
        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("Transaction not found"));
        // Ownership check
        if (entity.getUser() == null || !entity.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new IncorrectDataException("Unauthorized");
        }
        logger.info("Updating transaction {}  for user {}", patchDto, authenticatedEmail);
        // Apply non-null fields
        boolean changed = false;
        if (patchDto.getTitle() != null) {
            entity.setTitle(patchDto.getTitle());
            changed = true;
        }
        if (patchDto.getDescription() != null) {
            entity.setDescription(patchDto.getDescription());
            changed = true;
        }
        if (patchDto.getAmount() != null) {
            entity.setAmount(patchDto.getAmount());
            changed = true;
        }
        if (patchDto.getTransactionType() != null) {
            entity.setTransactionType(patchDto.getTransactionType());
            changed = true;
        }
        if (patchDto.getCategory() != null) {
            entity.setCategory(patchDto.getCategory());
            changed = true;
        }

        if (patchDto.getTransactionDate() != null) {
            entity.setTransactionDate(patchDto.getTransactionDate());
            changed = true;
        }

        UUID ZERO_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        if(entity.getClientId().equals(ZERO_UUID)) {
            entity.setClientId(UUID.fromString(patchDto.getClientId()));
            changed = true;
        }

        if (!changed) {
            return ResponseEntity.ok(new SuccessResponse("No changes supplied"));
        }
        entity.setUpdatedOn(System.currentTimeMillis());

        transactionRepository.save(entity);
        return ResponseEntity.ok(new SuccessResponse("Transaction updated successfully"));
    }

    @Transactional
    public ResponseEntity<SuccessResponse> deleteTransaction(Long id, String authenticatedEmail) throws IncorrectDataException {
        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("Transaction not found"));
        if (entity.getUser() == null || !entity.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new IncorrectDataException("Unauthorized");
        }
        transactionRepository.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Transaction deleted successfully"));
    }

    @Transactional
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(String authenticatedEmail) throws IncorrectDataException {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));
        Optional<List<TransactionEntity>> transactionsOpt = transactionRepository.findAllByUser(user);
        if(transactionsOpt.isEmpty()){
            return ResponseEntity.ok(List.of());
        }
        List<TransactionEntity> transactions = transactionsOpt.get();
        List<TransactionResponseDto> responseDtos = transactions.stream().map(tx -> {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setId(tx.getId());
            dto.setTitle(tx.getTitle());
            dto.setDescription(tx.getDescription());
            dto.setAmount(tx.getAmount());
            dto.setTransactionDate(tx.getTransactionDate());
            dto.setTransactionType(tx.getTransactionType().name());
            dto.setCategory(tx.getCategory());
            dto.setCreatedOn(tx.getCreatedOn());
            dto.setUpdatedOn(tx.getUpdatedOn());
            return dto;
        }).toList();
        return ResponseEntity.ok(responseDtos);
    }

    @Transactional
    public ResponseEntity<TransactionResponseDto> getTransactionById(Long id, String authenticatedEmail) throws IncorrectDataException {
        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("Transaction not found"));
        if (entity.getUser() == null || !entity.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new IncorrectDataException("Unauthorized");
        }
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setAmount(entity.getAmount());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setTransactionType(entity.getTransactionType().name());
        dto.setCategory(entity.getCategory());
        dto.setCreatedOn(entity.getCreatedOn());
        dto.setUpdatedOn(entity.getUpdatedOn());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsUpdatedSince(Long updatedTime, String authenticatedEmail) throws IncorrectDataException {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));
        Optional<List<TransactionEntity>> opt = transactionRepository.findAllByUserAndUpdatedOrCreatedSince(user, updatedTime);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<TransactionResponseDto> dtos = opt.get().stream().map(tx -> {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setId(tx.getId());
            dto.setTitle(tx.getTitle());
            dto.setDescription(tx.getDescription());
            dto.setCategory(tx.getCategory());
            dto.setAmount(tx.getAmount());
            dto.setTransactionDate(tx.getTransactionDate());
            dto.setTransactionType(tx.getTransactionType().name());
            dto.setCreatedOn(tx.getCreatedOn());
            dto.setUpdatedOn(tx.getUpdatedOn());
            dto.setClientId(tx.getClientId().toString());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }
}
