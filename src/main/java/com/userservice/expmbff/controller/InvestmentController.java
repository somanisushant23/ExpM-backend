package com.userservice.expmbff.controller;

import com.userservice.expmbff.dto.*;
import com.userservice.expmbff.exceptions.IncorrectDataException;
import com.userservice.expmbff.service.InvestmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investments")
public class InvestmentController {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    public ResponseEntity<List<InvestmentResponseDto>> createInvestment(
            @Valid @RequestBody List<InvestmentDto> investmentDtos,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Creating {} investments by {}", investmentDtos.size(), authenticatedEmail);
        return investmentService.createInvestments(investmentDtos, authenticatedEmail);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentResponseDto>> getAllInvestments(
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Fetching all investments for {}", authenticatedEmail);
        return investmentService.getAllInvestments(authenticatedEmail);
    }

    @PatchMapping
    public ResponseEntity<InvestmentResponseDto> updateInvestment(
            @Valid @RequestBody InvestmentDto investmentDtos,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Updating {} investments by {}", investmentDtos.getClientId(), authenticatedEmail);
        return investmentService.updateInvestment(investmentDtos, authenticatedEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse> deleteInvestment(
            @PathVariable("id") Long id,
            @RequestAttribute(name = "jwtSubject") String authenticatedEmail
    ) throws IncorrectDataException {
        logger.info("Deleting investment {} by {}", id, authenticatedEmail);
        return investmentService.deleteInvestment(id, authenticatedEmail);
    }
}
