package com.userservice.expmbff.repository;

import com.userservice.expmbff.entity.TransactionEntity;
import com.userservice.expmbff.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    TransactionEntity save(TransactionEntity transaction);

    Optional<TransactionEntity> findById(Long id);

    Optional<List<TransactionEntity>> findAllByUser(UserEntity user);

    @Query("SELECT t FROM TransactionEntity t WHERE t.user = :user AND (t.updatedOn >= :timestamp OR t.createdOn >= :timestamp)")
    Optional<List<TransactionEntity>> findAllByUserAndUpdatedOrCreatedSince(@Param("user") UserEntity user,
                                                                            @Param("timestamp") Long timestamp);

    void deleteById(Long id);

}
