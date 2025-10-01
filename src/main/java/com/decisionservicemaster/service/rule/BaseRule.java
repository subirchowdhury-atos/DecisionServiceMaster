package com.decisionservicemaster.service.rule;

import com.decisionservicemaster.domain.entity.Decision;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.service.report.ReportServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for business rules
 * Implements Template Method pattern
 */
public abstract class BaseRule {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseRule.class);
    protected final DecisionRequest decisionRequest;
    protected final Map<String, Object> config;
    protected final List<Object> reports;
    protected final ReportServiceFactory reportServiceFactory;
    
    public BaseRule(DecisionRequest decisionRequest, ReportServiceFactory reportServiceFactory) {
        this.decisionRequest = decisionRequest;
        this.reportServiceFactory = reportServiceFactory;
        this.config = loadConfig();
        this.reports = isEnabled() ? fetchReports() : new ArrayList<>();
    }
    
    /**
     * Main method to execute the rule
     * Creates a Decision entity based on rule evaluation
     */
    public void run() {
        if (!isEnabled()) {
            logger.debug("Rule {} is disabled", getRuleName());
            return;
        }
        
        Decision decision = new Decision(
            getRuleName(),
            getDecisionFromRule(),
            getMessage()
        );
        decision.setDecisionRequest(decisionRequest);
        decisionRequest.getDecisions().add(decision);
    }
    
    /**
     * Checks if rule is enabled based on config
     */
    protected boolean isEnabled() {
        if (config == null) return false;
        Object enabled = config.get("enabled");
        return enabled != null && (Boolean) enabled;
    }
    
    /**
     * Checks if report data is present
     */
    protected boolean dataPresent() {
        return reports != null && !reports.isEmpty() && reports.get(0) != null;
    }
    
    /**
     * Loads configuration from YAML based on address state/county
     */
    protected abstract Map<String, Object> loadConfig();
    
    /**
     * Returns list of report types required (e.g., ["Credit"], ["Mortgage"])
     */
    protected abstract List<String> getReportsRequired();
    
    /**
     * Evaluates the rule and returns decision (eligible, decline, unavailable)
     */
    protected abstract String getDecisionFromRule();
    
    /**
     * Returns the rule name
     */
    protected abstract String getRuleName();
    
    /**
     * Returns the decision message
     */
    protected abstract String getMessage();
    
    /**
     * Fetches required reports for this rule
     */
    private List<Object> fetchReports() {
        List<Object> fetchedReports = new ArrayList<>();
        
        for (String reportType : getReportsRequired()) {
            Object report = null;
            
            if ("Credit".equals(reportType)) {
                report = reportServiceFactory.fetchCreditReport(decisionRequest);
            } else if ("Mortgage".equals(reportType)) {
                report = reportServiceFactory.fetchMortgageReport(decisionRequest);
            }
            
            fetchedReports.add(report);
        }
        
        return fetchedReports;
    }
}

