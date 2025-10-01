package com.decisionservicemaster.controller.api.v1;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DecisionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    @Test
    public void testTokenIsConfigured() {
        String token = env.getProperty("api.token");
        System.out.println("Configured token: " + token);
        assertNotNull(token);
    }

    private Map<String, Object> getValidRequestData() {
        Map<String, Object> request = new HashMap<>();
        request.put("applicationId", 123);
        request.put("firstName", "john");
        request.put("lastName", "doe");
        request.put("ssn", "123456789");
        request.put("income", 10000);
        request.put("incomeType", "salary");
        request.put("requestedLoanAmount", 20000);
        
        Map<String, Object> address = new HashMap<>();
        address.put("street", "212 encounter bay");
        address.put("unitNumber", "123");
        address.put("city", "test_city");
        address.put("zip", "321");
        address.put("state", "California");
        address.put("county", "Alameda");
        
        request.put("address", address);
        
        return request;
    }

    @Test
    void testCreateReturns401WithInvalidToken() throws Exception {
        mockMvc.perform(post("/api/v1/decisions")
                .header("API-TOKEN", "not_foo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getValidRequestData())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("api token mismatch"));
    }

    @Test
    void testCreateReturns200WithValidAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/decisions")
                .header("API-TOKEN", "test-token-12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getValidRequestData())))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateReturnsDecisionWithValidAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/decisions")
                .header("API-TOKEN", "test-token-12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getValidRequestData())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application_id").value(123))
                .andExpect(jsonPath("$.address.street").value("212 encounter bay"))
                .andExpect(jsonPath("$.address.city").value("test_city"))
                .andExpect(jsonPath("$.address.zip").value("321"))
                .andExpect(jsonPath("$.address.state").value("California"))
                .andExpect(jsonPath("$.address.county").value("Alameda"))
                .andExpect(jsonPath("$.applicant.first_name").value("john"))
                .andExpect(jsonPath("$.applicant.last_name").value("doe"))
                .andExpect(jsonPath("$.applicant.ssn").value("123456789"))
                .andExpect(jsonPath("$.applicant.income").value(10000.0))
                .andExpect(jsonPath("$.applicant.requested_loan_amount").value(20000.0))
                .andExpect(jsonPath("$.final_decision").value("eligible"))
                .andExpect(jsonPath("$.decision", hasSize(2)))
                .andExpect(jsonPath("$.decision[0].rule_name").value("mortgage_rule"))
                .andExpect(jsonPath("$.decision[0].decision").value("decline"))
                .andExpect(jsonPath("$.decision[0].message").value("The outstanding mortgage loan on the applicants property is checked in relation with his income."))
                .andExpect(jsonPath("$.decision[1].rule_name").value("credit_rule"))
                .andExpect(jsonPath("$.decision[1].decision").value("eligible"))
                .andExpect(jsonPath("$.decision[1].message").value("The credit score of applicant is checked"))
                .andExpect(jsonPath("$.funding_options", hasSize(2)))
                .andExpect(jsonPath("$.funding_options[0].years").value(5))
                .andExpect(jsonPath("$.funding_options[0].interest_rate").value(6))
                .andExpect(jsonPath("$.funding_options[0].emi").value(100))
                .andExpect(jsonPath("$.funding_options[1].years").value(10))
                .andExpect(jsonPath("$.funding_options[1].interest_rate").value(6))
                .andExpect(jsonPath("$.funding_options[1].emi").value(60));
    }
}