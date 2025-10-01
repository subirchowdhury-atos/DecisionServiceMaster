package com.decisionservicemaster.service;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.Decision;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Service
public class MortgageRuleService {
    
    private Map<String, Object> mortgageRules;
    
    @PostConstruct
    public void init() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("rules/mortgage-rule.yml").getInputStream();
            mortgageRules = yaml.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load mortgage rules", e);
        }
    }
    
    /**
     * Evaluates mortgage rule for a decision request
     */
    public Decision evaluate(DecisionRequest decisionRequest) {
        Address address = decisionRequest.getPrimaryAddress();
        
        if (address == null) {
            return new Decision(
                "mortgage_rule",
                "unavailable",
                "Missing address information"
            );
        }
        
        String state = address.getState();
        String county = address.getCounty();
        
        // Get state rules
        Map<String, Object> stateRule = (Map<String, Object>) mortgageRules.get(state);
        
        if (stateRule == null) {
            return new Decision(
                "mortgage_rule",
                "unavailable",
                "Mortgage rule not available for state: " + state
            );
        }
        
        // Check if state or county is enabled
        if (!isRuleEnabled(stateRule, county)) {
            return new Decision(
                "mortgage_rule",
                "unavailable",
                "Mortgage rule not enabled for " + state + (county != null ? " - " + county : "")
            );
        }
        
        // Get mortgage threshold (county level or state level)
        Integer threshold = getMortgageThreshold(stateRule, county);
        
        if (threshold == null) {
            return new Decision(
                "mortgage_rule",
                "unavailable",
                "Mortgage threshold not configured for " + state + (county != null ? " - " + county : "")
            );
        }
        
        // Get actual mortgage count/total from MortgageReport
        Integer mortgageTotal = getTotalMortgage(address);
        
        if (mortgageTotal == null) {
            return new Decision(
                "mortgage_rule",
                "unavailable",
                "Mortgage information not available"
            );
        }
        
        // Evaluate
        if (mortgageTotal <= threshold) {
            return new Decision(
                "mortgage_rule",
                "eligible",
                "Total mortgage " + mortgageTotal + " is within threshold " + threshold
            );
        } else {
            return new Decision(
                "mortgage_rule",
                "decline",
                "Total mortgage " + mortgageTotal + " exceeds threshold " + threshold
            );
        }
    }
    
    private boolean isRuleEnabled(Map<String, Object> stateRule, String county) {
        if (county != null) {
            Map<String, Object> counties = (Map<String, Object>) stateRule.get("counties");
            if (counties != null && counties.containsKey(county)) {
                Map<String, Object> countyRule = (Map<String, Object>) counties.get(county);
                if (countyRule != null && countyRule.containsKey("enabled")) {
                    return (Boolean) countyRule.get("enabled");
                }
                return true;
            }
        }
        
        Object enabled = stateRule.get("enabled");
        return enabled != null && (Boolean) enabled;
    }
    
    private Integer getMortgageThreshold(Map<String, Object> stateRule, String county) {
        if (county != null) {
            Map<String, Object> counties = (Map<String, Object>) stateRule.get("counties");
            if (counties != null && counties.containsKey(county)) {
                Map<String, Object> countyRule = (Map<String, Object>) counties.get(county);
                if (countyRule != null && countyRule.containsKey("mortgage_threshold")) {
                    return (Integer) countyRule.get("mortgage_threshold");
                }
            }
        }
        
        return (Integer) stateRule.get("mortgage_threshold");
    }
    
    private Integer getTotalMortgage(Address address) {
        if (address.getMortgageReports() != null && !address.getMortgageReports().isEmpty()) {
            return address.getMortgageReports().get(0).getTotalMortgage();
        }
        return null;
    }
}