package com.decisionservicemaster.service.parser;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.MortgageReport;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Parser for property/mortgage report data
 * Extracts mortgage information from external API responses
 */
@Component
public class PropertyReportParser {
    
    /**
     * Parses mortgage report data from payload and creates a MortgageReport entity
     * 
     * @param payload Map containing mortgage data with keys:
     *                - "total_mortgage_amount"
     *                - "pending_mortgage_amount" 
     *                - "regular_in_payment"
     * @param address The address to associate the mortgage report with
     * @return MortgageReport entity populated with parsed data
     */
    public MortgageReport parse(Map<String, Object> payload, Address address) {
        if (payload == null || payload.isEmpty()) {
            return null;
        }
        
        MortgageReport mortgageReport = new MortgageReport(address);
        
        // Parse total mortgage
        Integer totalMortgage = extractInteger(payload, "total_mortgage_amount");
        mortgageReport.setTotalMortgage(totalMortgage);
        
        // Parse pending mortgage
        Integer pendingMortgage = extractInteger(payload, "pending_mortgage_amount");
        mortgageReport.setPendingMortgage(pendingMortgage);
        
        // Parse regular in payment
        String regularInPayment = extractString(payload, "regular_in_payment");
        mortgageReport.setRegularInPayment(regularInPayment);
        
        return mortgageReport;
    }
    
    /**
     * Parses mortgage data to Map format
     * 
     * @param payload Map containing mortgage data
     * @return Map with parsed mortgage data
     */
    public Map<String, Object> parseToMap(Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return Map.of();
        }
        
        return Map.of(
            "total_mortgage", extractInteger(payload, "total_mortgage_amount"),
            "pending_mortgage", extractInteger(payload, "pending_mortgage_amount"),
            "regular_in_payment", extractString(payload, "regular_in_payment")
        );
    }
    
    /**
     * Safely extracts Integer value from payload
     */
    private Integer extractInteger(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        
        if (value == null) {
            return null;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * Safely extracts String value from payload
     */
    private String extractString(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        return value != null ? value.toString() : null;
    }
}