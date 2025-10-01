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
public class PropertyDataService {
    
    private Map<String, Map<String, Object>> mockData;
    private final ObjectMapper objectMapper;
    
    public PropertyDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void init() {
        try {
            InputStream inputStream = new ClassPathResource("sample_data/property.json").getInputStream();
            mockData = objectMapper.readValue(
                inputStream, 
                new TypeReference<Map<String, Map<String, Object>>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load property mock data", e);
        }
    }
    
    public Map<String, Object> call(String street) {
        if (street == null || street.isEmpty()) {
            throw new ReportNotFoundException("Street address cannot be null or empty");
        }
        
        String normalizedStreet = normalizeStreet(street);
        Map<String, Object> propertyReport = mockData.get(normalizedStreet);
        
        if (propertyReport == null) {
            throw new ReportNotFoundException("Report not found for address: " + street);
        }
        
        return propertyReport;
    }
    
    private String normalizeStreet(String street) {
        if (street == null) return null;
        
        return street.toLowerCase()
                     .trim()
                     .replaceAll("[^a-z0-9]+", "_")
                     .replaceAll("_+", "_")
                     .replaceAll("^_|_$", "");
    }
    
    public static class ReportNotFoundException extends RuntimeException {
        public ReportNotFoundException(String message) {
            super(message);
        }
    }
}