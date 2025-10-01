package com.decisionservicemaster.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "credit_reports")
@Data
@NoArgsConstructor
@ToString(exclude = "applicant")
public class CreditReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "credit_score")
    private Integer creditScore;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;
    
    public CreditReport(Applicant applicant) {
        this.applicant = applicant;
    }
    
    public CreditReport(Applicant applicant, Integer creditScore) {
        this.applicant = applicant;
        this.creditScore = creditScore;
    }
}
