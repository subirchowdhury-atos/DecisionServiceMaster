package com.decisionservicemaster.service.parser;

import com.decisionservicemaster.domain.entity.Applicant;
import com.decisionservicemaster.domain.entity.CreditReport;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Parser for applicant-related report data
 * Extracts credit score information from external API responses
 */
@Component
public class ApplicantReportParser {
    
    /**
     * Parses credit report data from payload and creates a CreditReport entity
     * 
     * @param payload Map containing credit report data with "credit_score" key
     * @param applicant The applicant to associate the credit report with
     * @return CreditReport entity populated with parsed data
     */
    public CreditReport parse(Map<String, Object> payload, Applicant applicant) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        
        Integer creditScore = extractCreditScore(payload);
        
        if (creditScore == null) {
            return null;
        }
        
        CreditReport creditReport = new CreditReport(applicant, creditScore);
        return creditReport;
    }
    
    /**
     * Extracts credit score from payload
     * 
     * @param payload Map containing credit report data
     * @return Credit score as Integer, or null if not found
     */
    private Integer extractCreditScore(Map<String, Object> payload) {
        Object creditScoreObj = payload.get("credit_score");
        
        if (creditScoreObj == null) {
            return null;
        }
        
        if (creditScoreObj instanceof Number) {
            return ((Number) creditScoreObj).intValue();
        }
        
        if (creditScoreObj instanceof String) {
            try {
                return Integer.parseInt((String) creditScoreObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Parses only the credit score value from payload
     * Useful for quick parsing without creating entity
     * 
     * @param payload Map containing credit report data
     * @return Map with credit_score key
     */
    public Map<String, Object> parseToMap(Map<String, Object> payload) {
        Integer creditScore = extractCreditScore(payload);
        return Map.of("credit_score", creditScore != null ? creditScore : 0);
    }
}
