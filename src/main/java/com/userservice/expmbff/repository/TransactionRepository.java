package com.userservice.expmbff.repository;

import com.userservice.expmbff.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    TransactionEntity save(TransactionEntity transaction);

    Optional<TransactionEntity> findById(Long id);

    void deleteById(Long id);

}
