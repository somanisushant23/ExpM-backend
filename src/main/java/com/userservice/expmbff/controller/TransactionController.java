package com.userservice.expmbff.controller;

import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.TransactionDto;
import com.userservice.expmbff.service.TransactionService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<SuccessResponse> createTransaction(
            @Valid @RequestBody TransactionDto transactionDto,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail,
            @RequestAttribute(name = "jwtClaims") Claims jwtClaims
    ) {
        logger.info("Creating transaction for amount {} and title {} by {}", transactionDto.getAmount(), transactionDto.getTitle(), authenticatedEmail);
        // Example: access a custom claim, e.g., role -> jwtClaims.get("role", String.class)
        return transactionService.createTransaction(transactionDto);

    }
}
