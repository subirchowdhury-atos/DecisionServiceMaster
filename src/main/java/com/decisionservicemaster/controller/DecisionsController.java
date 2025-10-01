package com.decisionservicemaster.controller;

import com.decisionservicemaster.service.Processor;
import com.decisionservicemaster.service.RequestBuilder;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.dto.DecisionRequestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionsController {

    private final Processor processor;
    private final RequestBuilder requestBuilder;

    public DecisionsController(Processor processor, RequestBuilder requestBuilder) {
        this.processor = processor;
        this.requestBuilder = requestBuilder;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> requestBody) {
        Integer applicationId = parseApplicationId(requestBody);
        Map<String, Object> addressParams = extractAddressParams(requestBody);
        Map<String, Object> applicantParams = extractApplicantParams(requestBody);
        
        RequestBuilder builder = requestBuilder.buildDecisionRequest(
            applicationId,
            addressParams,
            applicantParams
        );

        if (!builder.isValid()) {
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("message", "Request not valid"));
        }

        DecisionRequest decision = processor.process(builder.getDecisionRequest());
        DecisionRequestResponse response = DecisionRequestResponse.from(decision);
        return ResponseEntity.ok(response);
    }

    private Integer parseApplicationId(Map<String, Object> requestBody) {
        if (requestBody.get("applicationId") == null) {
            return null;
        }
        
        Object appIdObj = requestBody.get("applicationId");
        if (appIdObj instanceof Number) {
            return ((Number) appIdObj).intValue();
        } else if (appIdObj instanceof String) {
            try {
                return Integer.parseInt((String) appIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Map<String, Object> extractAddressParams(Map<String, Object> requestBody) {
        Map<String, Object> addressMap = (Map<String, Object>) requestBody.get("address");
        
        if (addressMap == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> addressParams = new HashMap<>();
        addressParams.put("street", addressMap.get("street"));
        addressParams.put("unitNumber", addressMap.get("unitNumber"));
        addressParams.put("city", addressMap.get("city"));
        addressParams.put("zip", addressMap.get("zip"));
        addressParams.put("state", addressMap.get("state"));
        addressParams.put("county", addressMap.get("county"));
        
        return addressParams;
    }

    private Map<String, Object> extractApplicantParams(Map<String, Object> requestBody) {
        Map<String, Object> applicantParams = new HashMap<>();
        
        applicantParams.put("firstName", requestBody.get("firstName"));
        applicantParams.put("lastName", requestBody.get("lastName"));
        applicantParams.put("ssn", requestBody.get("ssn"));
        applicantParams.put("income", requestBody.get("income"));
        applicantParams.put("incomeType", requestBody.get("incomeType"));
        applicantParams.put("requestedLoanAmount", requestBody.get("requestedLoanAmount"));
        
        return applicantParams;
    }
}