package com.decisionservicemaster.service;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.Applicant;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.Decision;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Service
public class CreditRuleService {
    
    private Map<String, Object> creditRules;
    
    @PostConstruct
    public void init() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("rules/credit-rule.yml").getInputStream();
            creditRules = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load credit rules", e);
        }
    }
    
    /**
     * Evaluates credit rule for a decision request
     */
    public Decision evaluate(DecisionRequest decisionRequest) {
        Address address = decisionRequest.getPrimaryAddress();
        Applicant applicant = decisionRequest.getPrimaryApplicant();
        
        if (address == null || applicant == null) {
            return new Decision(
                "credit_rule",
                "unavailable",
                "Missing address or applicant information"
            );
        }
        
        String state = address.getState();
        String county = address.getCounty();
        
        // Get state rules
        Map<String, Object> stateRule = (Map<String, Object>) creditRules.get(state);
        
        if (stateRule == null || !isEnabled(stateRule)) {
            return new Decision(
                "credit_rule",
                "unavailable",
                "Credit rule not available for state: " + state
            );
        }
        
        // Get credit score threshold (state level or county level)
        Integer threshold = getCreditScoreThreshold(stateRule, county);
        
        if (threshold == null) {
            return new Decision(
                "credit_rule",
                "unavailable",
                "Credit threshold not configured for " + state + " - " + county
            );
        }
        
        // Get actual credit score from CreditReport
        Integer creditScore = getCreditScore(applicant);
        
        if (creditScore == null) {
            return new Decision(
                "credit_rule",
                "unavailable",
                "Credit score not available"
            );
        }
        
        // Evaluate
        if (creditScore >= threshold) {
            return new Decision(
                "credit_rule",
                "eligible",
                "Credit score " + creditScore + " meets threshold " + threshold
            );
        } else {
            return new Decision(
                "credit_rule",
                "decline",
                "Credit score " + creditScore + " below threshold " + threshold
            );
        }
    }
    
    private boolean isEnabled(Map<String, Object> rule) {
        Object enabled = rule.get("enabled");
        return enabled != null && (Boolean) enabled;
    }
    
    private Integer getCreditScoreThreshold(Map<String, Object> stateRule, String county) {
        // Check county-specific threshold first
        if (county != null) {
            Map<String, Object> counties = (Map<String, Object>) stateRule.get("counties");
            if (counties != null && counties.containsKey(county)) {
                Map<String, Object> countyRule = (Map<String, Object>) counties.get(county);
                if (countyRule != null && countyRule.containsKey("credit_score_threshold")) {
                    return (Integer) countyRule.get("credit_score_threshold");
                }
            }
        }
        
        // Fall back to state-level threshold
        return (Integer) stateRule.get("credit_score_threshold");
    }
    
    private Integer getCreditScore(Applicant applicant) {
        if (applicant.getCreditReports() != null && !applicant.getCreditReports().isEmpty()) {
            return applicant.getCreditReports().get(0).getCreditScore();
        }
        return null;
    }
}