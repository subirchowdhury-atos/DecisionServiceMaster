package com.decisionservicemaster.domain.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "decisions")
@Data
@NoArgsConstructor
@ToString(exclude = "decisionRequest")
public class Decision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "rule_name")
    private String ruleName;
    
    @Column(name = "decision")
    private String decision;
    
    @Column(name = "message")
    private String message;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_request_id", nullable = false)
    private DecisionRequest decisionRequest;
    
    public Decision(String ruleName, String decision, String message) {
        this.ruleName = ruleName;
        this.decision = decision;
        this.message = message;
    }
    
    public Map<String, String> serialize() {
        Map<String, String> serialized = new HashMap<>();
        serialized.put("rule_name", ruleName);
        serialized.put("decision", decision);
        serialized.put("message", message);
        return serialized;
    }
}