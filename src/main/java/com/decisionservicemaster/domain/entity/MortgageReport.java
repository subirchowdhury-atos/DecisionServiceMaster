package com.decisionservicemaster.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "mortgage_reports")
@Data
@NoArgsConstructor
@ToString(exclude = "address")
public class MortgageReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "total_mortgage")
    private Integer totalMortgage;
    
    @Column(name = "pending_mortgage")
    private Integer pendingMortgage;
    
    @Column(name = "regular_in_payment")
    private String regularInPayment;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    public MortgageReport(Address address) {
        this.address = address;
    }
}