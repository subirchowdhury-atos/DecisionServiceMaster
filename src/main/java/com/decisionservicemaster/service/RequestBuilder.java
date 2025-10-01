package com.decisionservicemaster.service;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.Applicant;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.repository.DecisionRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Builds and validates DecisionRequest entities with associated applicant and address
 */
@Service
public class RequestBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestBuilder.class);
    
    @Autowired
    private DecisionRequestRepository decisionRequestRepository;
    
    private DecisionRequest decisionRequest;
    private Address address;
    private Applicant applicant;
    private boolean valid = false;
    
    /**
     * Builds a DecisionRequest with applicant and address in a transaction
     * 
     * @param applicationId The application ID (Integer)
     * @param addressParams Map containing address parameters
     * @param applicantParams Map containing applicant parameters
     * @return RequestBuilder instance with valid flag set
     */
    @Transactional
    public RequestBuilder buildDecisionRequest(
            Integer applicationId, 
            Map<String, Object> addressParams,
            Map<String, Object> applicantParams) {
        
        try {
            // Create DecisionRequest
            this.decisionRequest = new DecisionRequest(applicationId);
            
            // Create Address
            this.address = buildAddress(addressParams);
            this.address.setDecisionRequest(this.decisionRequest);
            this.decisionRequest.getAddresses().add(this.address);
            
            // Create Applicant
            this.applicant = buildApplicant(applicantParams);
            this.applicant.setDecisionRequest(this.decisionRequest);
            this.decisionRequest.getApplicants().add(this.applicant);
            
            // Save to get IDs (transaction will persist)
            this.decisionRequest = decisionRequestRepository.save(this.decisionRequest);
            
            this.valid = true;
            
        } catch (Exception e) {
            logger.error("Failed to build decision request", e);
            this.valid = false;
        }
        
        return this;
    }
    
    /**
     * Checks if the DecisionRequest was successfully created
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Gets the created DecisionRequest
     */
    public DecisionRequest getDecisionRequest() {
        return decisionRequest;
    }
    
    /**
     * Gets the created Address
     */
    public Address getAddress() {
        return address;
    }
    
    /**
     * Gets the created Applicant
     */
    public Applicant getApplicant() {
        return applicant;
    }
    
    /**
     * Builds an Address entity from parameters
     */
    private Address buildAddress(Map<String, Object> addressParams) {
        if (addressParams == null || addressParams.isEmpty()) {
            throw new IllegalArgumentException("Address parameters cannot be null or empty");
        }
        
        Address address = new Address();
        address.setStreet((String) addressParams.get("street"));
        address.setUnitNumber((String) addressParams.get("unitNumber"));
        address.setCity((String) addressParams.get("city"));
        address.setZip((String) addressParams.get("zip"));
        address.setState((String) addressParams.get("state"));
        address.setCounty((String) addressParams.get("county"));
        
        return address;
    }
    
    /**
     * Builds an Applicant entity from parameters
     */
    private Applicant buildApplicant(Map<String, Object> applicantParams) {
        if (applicantParams == null || applicantParams.isEmpty()) {
            throw new IllegalArgumentException("Applicant parameters cannot be null or empty");
        }
        
        Applicant applicant = new Applicant();
        applicant.setFirstName((String) applicantParams.get("firstName"));
        applicant.setLastName((String) applicantParams.get("lastName"));
        
        // Handle SSN - store as encrypted (TODO: implement proper encryption)
        String ssn = (String) applicantParams.get("ssn");
        applicant.setEncryptedSsn(ssn);
        
        applicant.setIncomeType((String) applicantParams.get("incomeType"));
        
        // Handle income conversion to Double
        if (applicantParams.get("income") != null) {
            Object incomeObj = applicantParams.get("income");
            if (incomeObj instanceof Number) {
                applicant.setIncome(((Number) incomeObj).doubleValue());
            }
        }
        
        // Handle requested loan amount conversion to Double
        if (applicantParams.get("requestedLoanAmount") != null) {
            Object loanAmountObj = applicantParams.get("requestedLoanAmount");
            if (loanAmountObj instanceof Number) {
                applicant.setRequestedLoanAmount(((Number) loanAmountObj).doubleValue());
            }
        }
        
        return applicant;
    }
}