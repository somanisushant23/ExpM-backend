package com.userservice.expmbff.repository;

import com.userservice.expmbff.entity.InvestmentEntity;
import com.userservice.expmbff.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, Long> {

    InvestmentEntity save(InvestmentEntity investment);

    Optional<InvestmentEntity> findById(Long id);

    Optional<List<InvestmentEntity>> findAllByUser(UserEntity user);

    @Query("SELECT i FROM InvestmentEntity i WHERE i.user = :user AND (i.updatedOn >= :timestamp OR i.createdOn >= :timestamp)")
    Optional<List<InvestmentEntity>> findAllByUserAndUpdatedOrCreatedSince(@Param("user") UserEntity user,
                                                                           @Param("timestamp") Long timestamp);

    Optional<InvestmentEntity> findByClientId(java.util.UUID clientId);

    void deleteById(Long id);
}
