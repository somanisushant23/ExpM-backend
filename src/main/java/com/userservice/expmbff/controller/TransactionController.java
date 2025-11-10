package com.userservice.expmbff.controller;

import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.TransactionDto;
import com.userservice.expmbff.dto.TransactionResponseDto;
import com.userservice.expmbff.exceptions.IncorrectDataException;
import com.userservice.expmbff.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //@RequestAttribute(name = "jwtClaims") Claims jwtClaims
    @PostMapping
    public ResponseEntity<List<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody List<TransactionDto> transactionDtos,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Creating {} transactions by {}", transactionDtos.size(), authenticatedEmail);
        return transactionService.createTransactions(transactionDtos, authenticatedEmail);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SuccessResponse> patchTransaction(
            @PathVariable("id") Long id,
            @Valid @RequestBody TransactionDto patchDto,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Patching transaction {} by {} with payload {}", id, authenticatedEmail, patchDto);
        return transactionService.updateTransaction(id, patchDto, authenticatedEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteTransaction(
            @PathVariable("id") Long id,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Deleting transaction {} by {}", id, authenticatedEmail);
        return transactionService.deleteTransaction(id, authenticatedEmail);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Fetching all transactions for {}", authenticatedEmail);
        return transactionService.getAllTransactions(authenticatedEmail);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable("id") Long id,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Fetching transaction {} for {}", id, authenticatedEmail);
        return transactionService.getTransactionById(id, authenticatedEmail);
    }

    @GetMapping("/new-transactions")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsUpdatedSince(
            @RequestParam(name = "updatedTime") Long updatedTime,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Fetching transactions updated since {} for {}", updatedTime, authenticatedEmail);
        return transactionService.getTransactionsUpdatedSince(updatedTime, authenticatedEmail);
    }

}