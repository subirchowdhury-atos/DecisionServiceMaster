package com.decisionservicemaster.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@ToString(exclude = {"decisionRequest", "mortgageReports"})
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "street", nullable = false, columnDefinition = "TEXT")
    private String street;
    
    @Column(name = "unit_number", columnDefinition = "TEXT")
    private String unitNumber;
    
    @Column(name = "city", nullable = false, columnDefinition = "TEXT")
    private String city;
    
    @Column(name = "zip", nullable = false)
    private String zip;
    
    @Column(name = "state", nullable = false)
    private String state;
    
    @Column(name = "county", nullable = false)
    private String county;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_request_id", nullable = false)
    private DecisionRequest decisionRequest;
    
    @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MortgageReport> mortgageReports;
    
    public Address(String street, String unitNumber, String city, String zip, String state, String county) {
        this.street = street;
        this.unitNumber = unitNumber;
        this.city = city;
        this.zip = zip;
        this.state = state;
        this.county = county;
    }

    public List<MortgageReport> getMortgageReports() {
        if (mortgageReports == null) {
            mortgageReports = new java.util.ArrayList<>();
        }
        return mortgageReports;
    }
}