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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "decision_requests")
@Data
@NoArgsConstructor
@ToString(exclude = {"decisions", "applicants", "addresses"})
public class DecisionRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_id")
    private Integer applicationId;  // Changed from String to Integer
    
    @Column(name = "decision")
    private String decision;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "decisionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Decision> decisions;
    
    @OneToMany(mappedBy = "decisionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Applicant> applicants;
    
    @OneToMany(mappedBy = "decisionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;
    
    public DecisionRequest(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public List<Decision> getDecisions() {
        if (decisions == null) {
            decisions = new ArrayList<>();
        }
        return decisions;
    }

    public List<Applicant> getApplicants() {
        if (applicants == null) {
            applicants = new ArrayList<>();
        }
        return applicants;
    }

    public List<Address> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        return addresses;
    }
    
    @PrePersist
    @PreUpdate
    private void setDecision() {
        this.decision = isRuleWithDeclineOrUnavailable() ? "decline" : "eligible";
    }
    
    private boolean isRuleWithDeclineOrUnavailable() {
        if (decisions == null) return false;
        
        return decisions.stream().anyMatch(decision -> 
            "decline".equals(decision.getDecision()) || 
            "unavailable".equals(decision.getDecision())
        );
    }
    
    // Helper methods to get primary applicant and address
    public Applicant getPrimaryApplicant() {
        return applicants != null && !applicants.isEmpty() ? applicants.get(0) : null;
    }
    
    public Address getPrimaryAddress() {
        return addresses != null && !addresses.isEmpty() ? addresses.get(0) : null;
    }
}