package com.userservice.expmbff.service;

import com.userservice.expmbff.dto.InvestmentDto;
import com.userservice.expmbff.dto.InvestmentResponseDto;
import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.entity.InvestmentEntity;
import com.userservice.expmbff.entity.TransactionEntity;
import com.userservice.expmbff.entity.UserEntity;
import com.userservice.expmbff.exceptions.IncorrectDataException;
import com.userservice.expmbff.repository.InvestmentRepository;
import com.userservice.expmbff.repository.UserRepository;
import com.userservice.expmbff.utils.AppUtility;
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
public class InvestmentService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);
    private final InvestmentRepository investmentRepository;
    private final UserRepository userRepository;

    public InvestmentService(InvestmentRepository investmentRepository, UserRepository userRepository) {
        this.investmentRepository = investmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseEntity<List<InvestmentResponseDto>> createInvestments(List<InvestmentDto> investmentDtos, String authenticatedEmail)
            throws IncorrectDataException {
        // Find authenticated user
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));

        logger.info("Creating transaction {}  for user {}", investmentDtos, authenticatedEmail);
        List<InvestmentResponseDto> responseDtos = new ArrayList<>();
        for (InvestmentDto dto : investmentDtos) {
            // Map DTO to entity
            InvestmentEntity entity = new InvestmentEntity();
            entity.setInvestmentType(dto.getInvestmentType());
            entity.setAmount(dto.getAmount());
            entity.setExpectedReturnRate(dto.getExpectedReturnRate());
            entity.setCreationDate(dto.getCreationDate());
            entity.setMaturityDate(dto.getMaturityDate());
            entity.setDescription(dto.getDescription());
            entity.setCreatedOn(System.currentTimeMillis());
            entity.setUser(user);
            entity.setClientId(UUID.fromString(dto.getClientId()));
            if(dto.getCreatedOn() != null) {
                entity.setCreatedOn(dto.getCreatedOn());
            }
            InvestmentEntity savedEntity = investmentRepository.save(entity);
            InvestmentResponseDto responseDto = new InvestmentResponseDto();
            responseDto.setId(savedEntity.getId());
            responseDto.setClientId(savedEntity.getClientId().toString());
            responseDto.setInvestmentType(savedEntity.getInvestmentType());
            responseDto.setAmount(maskData(entity.getAmount().toString(), user));
            responseDto.setExpectedReturnRate(savedEntity.getExpectedReturnRate());
            responseDto.setCreatedOn(savedEntity.getCreatedOn());
            responseDto.setMaturityDate(savedEntity.getMaturityDate());
            responseDto.setDescription(savedEntity.getDescription());
            responseDto.setCreationDate(savedEntity.getCreationDate());
            responseDto.setUpdatedOn(savedEntity.getUpdatedOn());
            responseDtos.add(responseDto);
        }
        return ResponseEntity.ok(responseDtos);
    }

    @Transactional
    public ResponseEntity<List<InvestmentResponseDto>> getAllInvestments(String authenticatedEmail)
            throws IncorrectDataException {
        // Find authenticated user
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));

        logger.info("Fetching all investments for user {}", authenticatedEmail);
        Optional<List<InvestmentEntity>> optionalInvestments = investmentRepository.findAllByUser(user);
        if(optionalInvestments.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<InvestmentEntity> investments = optionalInvestments.get();
        List<InvestmentResponseDto> responseDtos = new ArrayList<>();
        for (InvestmentEntity entity : investments) {
            InvestmentResponseDto dto = new InvestmentResponseDto();
            dto.setId(entity.getId());
            dto.setClientId(entity.getClientId().toString());
            dto.setInvestmentType(entity.getInvestmentType());
            dto.setAmount(maskData(entity.getAmount().toString(), user));
            dto.setExpectedReturnRate(entity.getExpectedReturnRate());
            dto.setCreationDate(entity.getCreationDate());
            dto.setMaturityDate(entity.getMaturityDate());
            dto.setDescription(entity.getDescription());
            dto.setCreatedOn(entity.getCreatedOn());
            dto.setUpdatedOn(entity.getUpdatedOn());
            responseDtos.add(dto);
        }
        return ResponseEntity.ok(responseDtos);
    }

    @Transactional
    public ResponseEntity<InvestmentResponseDto> updateInvestment(InvestmentDto dto, String authenticatedEmail)
            throws IncorrectDataException {
        // Find authenticated user
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IncorrectDataException("Unauthorized"));

        logger.info("Updating transaction {}  for user {}", dto, authenticatedEmail);
        Optional<InvestmentEntity> existingEntity = investmentRepository.findByClientId(UUID.fromString(dto.getClientId()));
        if(existingEntity.isEmpty() || !existingEntity.get().getUser().getId().equals(user.getId())) {
            throw new IncorrectDataException("Investment not found");
        }

        InvestmentEntity entity = existingEntity.get();
        entity.setInvestmentType(dto.getInvestmentType());
        entity.setAmount(dto.getAmount());
        entity.setExpectedReturnRate(dto.getExpectedReturnRate());
        entity.setCreationDate(dto.getCreationDate());
        entity.setMaturityDate(dto.getMaturityDate());
        entity.setDescription(dto.getDescription());
        entity.setUpdatedOn(System.currentTimeMillis());
        entity.setClientId(UUID.fromString(dto.getClientId()));
        entity.setUser(user);
        InvestmentEntity savedEntity = investmentRepository.save(entity);
        InvestmentResponseDto responseDto = new InvestmentResponseDto();
        responseDto.setId(savedEntity.getId());
        responseDto.setClientId(savedEntity.getClientId().toString());
        responseDto.setInvestmentType(savedEntity.getInvestmentType());
        responseDto.setAmount(maskData(entity.getAmount().toString(), user));
        responseDto.setExpectedReturnRate(savedEntity.getExpectedReturnRate());
        responseDto.setCreatedOn(savedEntity.getCreatedOn());
        responseDto.setMaturityDate(savedEntity.getMaturityDate());
        responseDto.setDescription(savedEntity.getDescription());
        responseDto.setCreationDate(savedEntity.getCreationDate());
        responseDto.setUpdatedOn(savedEntity.getUpdatedOn());
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    public ResponseEntity<SuccessResponse> deleteInvestment(Long id, String authenticatedEmail) throws IncorrectDataException {
        InvestmentEntity entity = investmentRepository.findById(id)
                .orElseThrow(() -> new IncorrectDataException("Transaction not found"));
        if (entity.getUser() == null || !entity.getUser().getEmail().equalsIgnoreCase(authenticatedEmail)) {
            throw new IncorrectDataException("Unauthorized");
        }
        investmentRepository.deleteById(id);
        return ResponseEntity.ok(new SuccessResponse("Investment deleted successfully"));
    }

    private String maskData(String data, UserEntity user) {
        return AppUtility.maskDataAfter(user, data, AppUtility.MASKING_DEFAULT_VISIBLE_CHARS);
    }
}
