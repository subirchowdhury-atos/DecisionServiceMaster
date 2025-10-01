package com.decisionservicemaster.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "applicants")
@Data
@NoArgsConstructor
@ToString(exclude = {"decisionRequest", "creditReports"})
public class Applicant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "encrypted_ssn", nullable = false)
    private String encryptedSsn;
    
    @Column(name = "encrypted_ssn_iv")
    private String encryptedSsnIv;
    
    @Column(name = "income", nullable = false)
    private Double income;  
    
    @Column(name = "income_type")
    private String incomeType;
    
    @Column(name = "requested_loan_amount", nullable = false)
    private Double requestedLoanAmount;  // Changed to Double
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_request_id", nullable = false)
    private DecisionRequest decisionRequest;
    
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditReport> creditReports;
    
    public Applicant(String firstName, String lastName, String encryptedSsn, Double income, String incomeType, Double requestedLoanAmount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.encryptedSsn = encryptedSsn;
        this.income = income;
        this.incomeType = incomeType;
        this.requestedLoanAmount = requestedLoanAmount;
    }

    public List<CreditReport> getCreditReports() {
        if (creditReports == null) {
            creditReports = new ArrayList<>();
        }
        return creditReports;
    }
}
