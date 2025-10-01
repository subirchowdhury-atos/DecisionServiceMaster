package com.decisionservicemaster.dto;

import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.Applicant;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class DecisionRequestResponse {
    
    @JsonProperty("application_id")
    private Integer applicationId;
    
    @JsonProperty("address")
    private Map<String, String> address;
    
    @JsonProperty("applicant")
    private Map<String, Object> applicant;
    
    @JsonProperty("final_decision")
    private String finalDecision;
    
    @JsonProperty("decision")
    private List<Map<String, String>> decision;
    
    @JsonProperty("funding_options")
    private List<Map<String, Object>> fundingOptions;
    
    // Static factory method to create response from entity
    public static DecisionRequestResponse from(DecisionRequest decisionRequest) {
        DecisionRequestResponse response = new DecisionRequestResponse();
        
        response.setApplicationId(decisionRequest.getApplicationId());
        response.setAddress(serializeAddress(decisionRequest.getPrimaryAddress()));
        response.setApplicant(serializeApplicant(decisionRequest.getPrimaryApplicant()));
        response.setFinalDecision(decisionRequest.getDecision());
        response.setDecision(serializeDecisions(decisionRequest));
        response.setFundingOptions(getFundingOptions(decisionRequest));
        
        return response;
    }
    
    private static Map<String, String> serializeAddress(Address address) {
        Map<String, String> addressMap = new HashMap<>();
        if (address != null) {
            addressMap.put("street", address.getStreet());
            addressMap.put("unit_number", address.getUnitNumber());
            addressMap.put("city", address.getCity());
            addressMap.put("zip", address.getZip());
            addressMap.put("state", address.getState());
            addressMap.put("county", address.getCounty());
        }
        return addressMap;
    }
    
    private static Map<String, Object> serializeApplicant(Applicant applicant) {
        Map<String, Object> applicantMap = new HashMap<>();
        if (applicant != null) {
            applicantMap.put("first_name", applicant.getFirstName());
            applicantMap.put("last_name", applicant.getLastName());
            applicantMap.put("ssn", applicant.getEncryptedSsn()); // Return encrypted SSN
            applicantMap.put("income", applicant.getIncome());
            applicantMap.put("income_type", applicant.getIncomeType());
            applicantMap.put("requested_loan_amount", applicant.getRequestedLoanAmount());
        }
        return applicantMap;
    }
    
    private static List<Map<String, String>> serializeDecisions(DecisionRequest decisionRequest) {
        List<Map<String, String>> decisionsList = new ArrayList<>();
        if (decisionRequest.getDecisions() != null) {
            decisionRequest.getDecisions().forEach(decision -> {
                Map<String, String> decisionMap = new HashMap<>();
                decisionMap.put("rule_name", decision.getRuleName());
                decisionMap.put("decision", decision.getDecision());
                decisionMap.put("message", decision.getMessage());
                decisionsList.add(decisionMap);
            });
        }
        return decisionsList;
    }
    
    private static List<Map<String, Object>> getFundingOptions(DecisionRequest decisionRequest) {
        List<Map<String, Object>> options = new ArrayList<>();
        
        if ("eligible".equals(decisionRequest.getDecision())) {
            Map<String, Object> option1 = new HashMap<>();
            option1.put("years", 5);
            option1.put("interest_rate", 6);
            option1.put("emi", 100);
            options.add(option1);
            
            Map<String, Object> option2 = new HashMap<>();
            option2.put("years", 10);
            option2.put("interest_rate", 6);
            option2.put("emi", 60);
            options.add(option2);
        }
        
        return options;
    }
}