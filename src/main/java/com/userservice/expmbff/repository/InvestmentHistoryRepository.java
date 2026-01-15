package com.userservice.expmbff.repository;

import com.userservice.expmbff.entity.InvestmentHistoryEntity;
import com.userservice.expmbff.entity.InvestmentHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentHistoryRepository extends JpaRepository<InvestmentHistoryEntity, InvestmentHistoryKey> {

    /**
     * Check if any entry exists for the given month, year, and investment type
     *
     * @param investmentHistoryKey Composite key containing month, year, and investment type
     * @return true if entry exists, false otherwise
     */
    boolean existsById(InvestmentHistoryKey investmentHistoryKey);
}
