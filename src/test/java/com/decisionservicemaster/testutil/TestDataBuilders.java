package com.decisionservicemaster.testutil;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.Applicant;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.Decision;
import com.decisionservicemaster.domain.entity.MortgageReport;
import com.decisionservicemaster.domain.entity.CreditReport;

/**
 * Test data builders for creating test entities
 */
public class TestDataBuilders {
    
    // ========== Address Builders ==========
    
    public static Address defaultAddress(DecisionRequest decisionRequest) {
        Address address = new Address();
        address.setStreet("212 encounter bay");
        address.setCity("bay view");
        address.setCounty("chapara");
        address.setState("CA");
        address.setZip("12345");
        address.setDecisionRequest(decisionRequest);
        return address;
    }
    
    public static Address addressWithOtherStreet(DecisionRequest decisionRequest) {
        Address address = defaultAddress(decisionRequest);
        address.setStreet("123 encounter bay");
        return address;
    }
    
    // ========== Applicant Builders ==========
    
    public static Applicant defaultApplicant(DecisionRequest decisionRequest) {
        Applicant applicant = new Applicant();
        applicant.setFirstName("first");
        applicant.setLastName("last");
        applicant.setEncryptedSsn("123456789");
        applicant.setIncome(10000.0);
        applicant.setIncomeType("salary");
        applicant.setRequestedLoanAmount(100000.0);
        applicant.setDecisionRequest(decisionRequest);
        return applicant;
    }
    
    public static Applicant applicantWithDifferentIncome(DecisionRequest decisionRequest) {
        Applicant applicant = defaultApplicant(decisionRequest);
        applicant.setIncome(9999.0);
        return applicant;
    }
    
    // ========== DecisionRequest Builder ==========
    
    public static DecisionRequest defaultDecisionRequest() {
        return new DecisionRequest(123);
    }
    
    // ========== Complete Request Builder ==========
    
    public static DecisionRequest completeDecisionRequest() {
        DecisionRequest decisionRequest = defaultDecisionRequest();
        
        Address address = defaultAddress(decisionRequest);
        decisionRequest.getAddresses().add(address);
        
        Applicant applicant = defaultApplicant(decisionRequest);
        decisionRequest.getApplicants().add(applicant);
        
        return decisionRequest;
    }
    
    // ========== CreditReport Builders ==========
    
    public static CreditReport defaultCreditReport(Applicant applicant) {
        CreditReport creditReport = new CreditReport();
        creditReport.setCreditScore(6);
        creditReport.setApplicant(applicant);
        return creditReport;
    }
    
    public static CreditReport badCreditReport(Applicant applicant) {
        CreditReport creditReport = new CreditReport();
        creditReport.setCreditScore(2);
        creditReport.setApplicant(applicant);
        return creditReport;
    }
    
    // ========== MortgageReport Builders ==========
    
    public static MortgageReport defaultMortgageReport(Address address) {
        MortgageReport mortgageReport = new MortgageReport();
        mortgageReport.setTotalMortgage(10000);
        mortgageReport.setPendingMortgage(100000);
        mortgageReport.setRegularInPayment("regular");
        mortgageReport.setAddress(address);
        return mortgageReport;
    }
    
    public static MortgageReport failingMortgageReport(Address address) {
        MortgageReport mortgageReport = defaultMortgageReport(address);
        mortgageReport.setPendingMortgage(100001);
        return mortgageReport;
    }
    
    // ========== Decision Builders ==========
    
    public static Decision defaultDecision(DecisionRequest decisionRequest) {
        Decision decision = new Decision();
        decision.setRuleName("test_rule");
        decision.setDecision("eligible");
        decision.setMessage("you are eligible");
        decision.setDecisionRequest(decisionRequest);
        return decision;
    }
    
    public static Decision declineDecision(DecisionRequest decisionRequest) {
        Decision decision = defaultDecision(decisionRequest);
        decision.setDecision("decline");
        decision.setMessage("you are not eligible");
        return decision;
    }
}