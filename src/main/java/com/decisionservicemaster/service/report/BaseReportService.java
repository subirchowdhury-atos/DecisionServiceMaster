package com.decisionservicemaster.service.report;

import com.decisionservicemaster.domain.entity.DecisionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Abstract base class for fetching and parsing external reports
 * Implements Template Method pattern
 */
public abstract class BaseReportService<T> {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseReportService.class);
    
    /**
     * Main method to fetch and create report entity
     * Template method that orchestrates the process
     * 
     * @param decisionRequest The decision request context
     * @return Report entity (CreditReport or MortgageReport), or null if failed
     */
    public T fetch(DecisionRequest decisionRequest) {
        try {
            // Call external service to get raw data
            Map<String, Object> serviceData = callService(decisionRequest);
            
            // Parse the data
            Map<String, Object> parsedData = parseData(serviceData);
            
            // Create and save report entity
            T report = createReport(decisionRequest, parsedData);
            
            if (saveReport(decisionRequest, report)) {
                return report;
            } else {
                logger.error("Failed to save report for decision request: {}", decisionRequest.getId());
                return null;
            }
            
        } catch (ApplicantDataService.ReportNotFoundException | 
                 PropertyDataService.ReportNotFoundException e) {
            logger.error("Report not found for decision request: {} - {}", 
                        decisionRequest.getId(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching report for decision request: {}", 
                        decisionRequest.getId(), e);
            return null;
        }
    }
    
    /**
     * Calls the appropriate external service to fetch report data
     */
    protected abstract Map<String, Object> callService(DecisionRequest decisionRequest);
    
    /**
     * Parses the raw service data using appropriate parser
     */
    protected abstract Map<String, Object> parseData(Map<String, Object> serviceData);
    
    /**
     * Creates the report entity with parsed data and additional fields
     */
    protected abstract T createReport(DecisionRequest decisionRequest, Map<String, Object> parsedData);
    
    /**
     * Saves the report entity (handled by JPA cascade in most cases)
     */
    protected abstract boolean saveReport(DecisionRequest decisionRequest, T report);
}