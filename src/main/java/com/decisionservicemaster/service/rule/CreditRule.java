package com.decisionservicemaster.service.rule;

import com.decisionservicemaster.domain.entity.CreditReport;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.service.report.ReportServiceFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Credit rule implementation
 * Checks if credit score meets threshold
 */
public class CreditRule extends BaseRule {
    
    private Map<String, Object> ruleConfig;
    
    public CreditRule(DecisionRequest decisionRequest, ReportServiceFactory reportServiceFactory) {
        super(decisionRequest, reportServiceFactory);
    }
    
    @Override
    protected Map<String, Object> loadConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("rules/credit-rule.yml").getInputStream();
            Map<String, Object> allConfigs = yaml.load(inputStream);
            
            String state = decisionRequest.getPrimaryAddress().getState();
            String county = decisionRequest.getPrimaryAddress().getCounty();
            
            ruleConfig = RulesConfigHelper.getConfig(allConfigs, state, county);
            return ruleConfig;
        } catch (Exception e) {
            logger.error("Failed to load credit rule config", e);
            return Map.of();
        }
    }
    
    @Override
    protected List<String> getReportsRequired() {
        return List.of("Credit");
    }
    
    @Override
    protected String getDecisionFromRule() {
        if (!dataPresent()) {
            return "unavailable";
        }
        
        if (creditScoreIsBelowThreshold()) {
            return "decline";
        }
        
        return "eligible";
    }
    
    @Override
    protected String getRuleName() {
        return "credit_rule";
    }
    
    @Override
    protected String getMessage() {
        return "The credit score of applicant is checked";
    }
    
    private boolean creditScoreIsBelowThreshold() {
        CreditReport creditReport = (CreditReport) reports.get(0);
        Integer threshold = getCreditScoreThreshold();
        
        if (creditReport == null || creditReport.getCreditScore() == null) {
            return true; // If no credit score, consider it below threshold
        }
        
        return creditReport.getCreditScore() <= threshold;
    }
    
    private Integer getCreditScoreThreshold() {
        Object threshold = ruleConfig.get("credit_score_threshold");
        return threshold != null ? (Integer) threshold : 0;
    }
}