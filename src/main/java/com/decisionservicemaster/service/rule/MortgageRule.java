package com.decisionservicemaster.service.rule;

import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.MortgageReport;
import com.decisionservicemaster.service.report.ReportServiceFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Mortgage rule implementation
 * Checks if pending mortgage is below threshold based on income
 */
public class MortgageRule extends BaseRule {
    
    private Map<String, Object> ruleConfig;
    
    public MortgageRule(DecisionRequest decisionRequest, ReportServiceFactory reportServiceFactory) {
        super(decisionRequest, reportServiceFactory);
    }
    
    @Override
    protected Map<String, Object> loadConfig() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("rules/mortgage-rule.yml").getInputStream();
            Map<String, Object> allConfigs = yaml.load(inputStream);
            
            String state = decisionRequest.getPrimaryAddress().getState();
            String county = decisionRequest.getPrimaryAddress().getCounty();
            
            ruleConfig = RulesConfigHelper.getConfig(allConfigs, state, county);
            return ruleConfig;
        } catch (Exception e) {
            logger.error("Failed to load mortgage rule config", e);
            return Map.of();
        }
    }
    
    @Override
    protected List<String> getReportsRequired() {
        return List.of("Mortgage");
    }
    
    @Override
    protected String getDecisionFromRule() {
        if (!dataPresent()) {
            return "unavailable";
        }
        
        if (!mortgageIsBelowThreshold()) {
            return "decline";
        }
        
        return "eligible";
    }
    
    @Override
    protected String getRuleName() {
        return "mortgage_rule";
    }
    
    @Override
    protected String getMessage() {
        return "The outstanding mortgage loan on the applicants property " +
               "is checked in relation with his income.";
    }
    
    private boolean mortgageIsBelowThreshold() {
        MortgageReport mortgageReport = (MortgageReport) reports.get(0);
        Double applicantIncome = decisionRequest.getPrimaryApplicant().getIncome();
        Integer mortgageThreshold = getMortgageThreshold();
        
        if (mortgageReport == null || mortgageReport.getPendingMortgage() == null) {
            return false;
        }
        
        return mortgageReport.getPendingMortgage() <= (applicantIncome * mortgageThreshold);
    }
    
    private Integer getMortgageThreshold() {
        Object threshold = ruleConfig.get("mortgage_threshold");
        return threshold != null ? (Integer) threshold : 0;
    }
}
