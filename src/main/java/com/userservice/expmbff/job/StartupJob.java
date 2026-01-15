package com.userservice.expmbff.job;

import com.userservice.expmbff.entity.InvestmentEntity;
import com.userservice.expmbff.entity.InvestmentHistoryEntity;
import com.userservice.expmbff.entity.InvestmentHistoryKey;
import com.userservice.expmbff.entity.enums.InvestmentType;
import com.userservice.expmbff.repository.InvestmentHistoryRepository;
import com.userservice.expmbff.repository.InvestmentRepository;
import com.userservice.expmbff.utils.AppUtility;
import com.userservice.expmbff.utils.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Background job that runs on application startup.
 * Implement ApplicationRunner to execute initialization logic when the app starts.
 */
@Component
public class StartupJob implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupJob.class);

    @Autowired
    private InvestmentHistoryRepository investmentHistoryRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    /**
     * This method runs automatically when the application starts.
     * Add your startup logic here.
     */
    @Override
    public void run(ApplicationArguments args) {
        logger.info("========================================");
        logger.info("Starting background job on application startup");

        try {
            processMonthlyInvestmentHistory();
            logger.info("========================================");
            logger.info("Background job completed successfully");
            logger.info("========================================");
        } catch (Exception e) {
            logger.error("Error during startup job execution", e);
            // Decide whether to throw or continue based on your requirements
            // throw new RuntimeException("Startup job failed", e);
        }
    }

    /**
     * Check for current month/year entries in InvestmentHistoryEntity.
     * If not found, calculate sum of investment amounts by type and store in DB.
     */
    private void processMonthlyInvestmentHistory() {
        try {
            logger.info("Processing monthly investment history...");

            // Get current month and year
            YearMonth currentYearMonth = YearMonth.now();
            String currentMonth = String.format("%02d", currentYearMonth.getMonthValue());
            String currentYear = String.valueOf(currentYearMonth.getYear());

            logger.info("Checking for entries in month: {} and year: {}", currentMonth, currentYear);

            // Check if entries exist for all investment types in the current month and year
            for (InvestmentType investmentType : InvestmentType.values()) {
                long monthTimestamp = AppUtility.getTimestampForMonth(currentMonth, currentYear);
                com.userservice.expmbff.entity.InvestmentHistoryKey key = new com.userservice.expmbff.entity.InvestmentHistoryKey(monthTimestamp, investmentType);
                //boolean typeExists = investmentHistoryRepository.existsById(key);
                // Get all investments
                List<InvestmentEntity> allInvestments = investmentRepository.findAll();

                if (allInvestments.isEmpty()) {
                    logger.info("No investments found in the database.");
                    return;
                }

                // Group investments by type and calculate sum
                Map<InvestmentType, BigDecimal> investmentSumByType = calculateInvestmentSumByType(allInvestments);

                // Save to InvestmentHistoryEntity
                saveInvestmentHistory(investmentSumByType, currentMonth, currentYear);

                logger.info("Monthly investment history processed successfully for month: {} and year: {}", currentMonth, currentYear);
            }


        } catch (Exception e) {
            logger.error("Error processing monthly investment history", e);
        }
    }

    /**
     * Calculate sum of investments grouped by investment type
     * Decrypts encrypted amounts if necessary
     *
     * @param investments List of investments
     * @return Map of InvestmentType to sum amount
     */
    private Map<InvestmentType, BigDecimal> calculateInvestmentSumByType(List<InvestmentEntity> investments) {
        Map<InvestmentType, BigDecimal> resultMap = new HashMap<>();

        for (InvestmentEntity investment : investments) {
            InvestmentType type = investment.getInvestmentType();
            String encryptedAmount = investment.getAmount();

            try {
                // Decrypt the amount
                String decryptedAmount = EncryptUtil.decrypt(encryptedAmount);
                BigDecimal amount = new BigDecimal(decryptedAmount);

                // Add to the sum for this type
                resultMap.put(type, resultMap.getOrDefault(type, BigDecimal.ZERO).add(amount));

                logger.debug("Processed investment type: {}, amount: {}", type, amount);
            } catch (Exception e) {
                logger.warn("Error decrypting amount for investment type: {}. Using encrypted value as is.", type, e);
                // Fallback: try to parse as is (in case it's not encrypted)
                try {
                    BigDecimal amount = new BigDecimal(encryptedAmount);
                    resultMap.put(type, resultMap.getOrDefault(type, BigDecimal.ZERO).add(amount));
                } catch (NumberFormatException nfe) {
                    logger.error("Could not parse amount for investment type: {}", type, nfe);
                }
            }
        }

        logger.info("Calculated investment sums by type: {}", resultMap);
        return resultMap;
    }

    /**
     * Save investment history to database
     *
     * @param investmentSumByType Map of InvestmentType to sum amount
     * @param month Current month (MM format)
     * @param year Current year (YYYY format)
     */
    private void saveInvestmentHistory(Map<InvestmentType, BigDecimal> investmentSumByType, String month, String year) {
        for (Map.Entry<InvestmentType, BigDecimal> entry : investmentSumByType.entrySet()) {
            try {
                // Create the composite key with month, year, and investment type
                long monthTimestamp = AppUtility.getTimestampForMonth(month, year);
                InvestmentHistoryKey key = new com.userservice.expmbff.entity.InvestmentHistoryKey(monthTimestamp, entry.getKey());

                InvestmentHistoryEntity historyEntity = new InvestmentHistoryEntity();
                historyEntity.setInvestmentHistoryKey(key);

                // Encrypt the amount before storing
                String encryptedAmount = EncryptUtil.encrypt(entry.getValue().toPlainString());
                historyEntity.setAmount(encryptedAmount);

                investmentHistoryRepository.save(historyEntity);
                logger.info("Saved investment history for type: {} with amount: {}", entry.getKey(), entry.getValue());
            } catch (Exception e) {
                logger.error("Error saving investment history for type: {}", entry.getKey(), e);
            }
        }
    }
}

