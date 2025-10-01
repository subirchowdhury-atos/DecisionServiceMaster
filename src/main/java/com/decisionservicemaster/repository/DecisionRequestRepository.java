package com.decisionservicemaster.repository;

import com.decisionservicemaster.domain.entity.DecisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DecisionRequestRepository extends JpaRepository<DecisionRequest, Long> {
    
    Optional<DecisionRequest> findByApplicationId(Integer applicationId);
    
    List<DecisionRequest> findByDecision(String decision);
    
    /**
     * Find DecisionRequest with all related entities eagerly loaded
     * This prevents N+1 query issues
     */
    @Query("SELECT dr FROM DecisionRequest dr " +
           "LEFT JOIN FETCH dr.decisions " +
           "WHERE dr.id = :id")
    Optional<DecisionRequest> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Find DecisionRequest by application ID with all relationships
     */
    @Query("SELECT dr FROM DecisionRequest dr " +
           "LEFT JOIN FETCH dr.decisions " +
           "WHERE dr.applicationId = :applicationId")
    Optional<DecisionRequest> findByApplicationIdWithRelations(@Param("applicationId") Integer applicationId);
    
    boolean existsByApplicationId(Integer applicationId);
}