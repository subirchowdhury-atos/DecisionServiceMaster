package com.decisionservicemaster.service;

import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.repository.DecisionRequestRepository;
import com.decisionservicemaster.service.report.ReportServiceFactory;
import com.decisionservicemaster.service.rule.BaseRule;
import com.decisionservicemaster.service.rule.CreditRule;
import com.decisionservicemaster.service.rule.MortgageRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class Processor {
    
    private static final List<String> RULE_SET = List.of("mortgage_rule", "credit_rule");
    
    @Autowired
    private DecisionRequestRepository decisionRequestRepository;
    
    @Autowired
    private ReportServiceFactory reportServiceFactory;
    
    /**
     * Processes a decision request by running all business rules
     * 
     * @param decisionRequest The decision request to process
     * @return The processed and saved DecisionRequest with all decisions
     */
    @Transactional
    public DecisionRequest process(DecisionRequest decisionRequest) {
        // Execute all rules in order
        for (String ruleName : RULE_SET) {
            BaseRule rule = initRule(ruleName, decisionRequest);
            rule.run();
        }
        
        // Save and return
        return decisionRequestRepository.save(decisionRequest);
    }
    
    private BaseRule initRule(String ruleName, DecisionRequest decisionRequest) {
        return switch (ruleName) {
            case "mortgage_rule" -> new MortgageRule(decisionRequest, reportServiceFactory);
            case "credit_rule" -> new CreditRule(decisionRequest, reportServiceFactory);
            default -> throw new IllegalArgumentException("Unknown rule: " + ruleName);
        };
    }
}