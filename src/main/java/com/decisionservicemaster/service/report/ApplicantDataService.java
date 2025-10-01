package com.decisionservicemaster.service.report;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class ApplicantDataService {
    
    private Map<String, Map<String, Object>> mockData;
    private final ObjectMapper objectMapper;
    
    public ApplicantDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void init() {
        try {
            InputStream inputStream = new ClassPathResource("sample_data/applicant.json").getInputStream();
            mockData = objectMapper.readValue(
                inputStream, 
                new TypeReference<Map<String, Map<String, Object>>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load applicant mock data", e);
        }
    }
    
    public Map<String, Object> call(String ssn) {
        if (ssn == null || ssn.isEmpty()) {
            throw new ReportNotFoundException("SSN cannot be null or empty");
        }
        
        Map<String, Object> applicantReport = mockData.get(ssn);
        
        if (applicantReport == null) {
            throw new ReportNotFoundException("Report not found for SSN: " + ssn);
        }
        
        return applicantReport;
    }
    
    public static class ReportNotFoundException extends RuntimeException {
        public ReportNotFoundException(String message) {
            super(message);
        }
    }
}