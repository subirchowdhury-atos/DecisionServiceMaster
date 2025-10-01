package com.decisionservicemaster.service.report;

import com.decisionservicemaster.domain.entity.Address;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.MortgageReport;
import com.decisionservicemaster.service.parser.PropertyReportParser;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service to fetch and create mortgage reports for addresses
 */
@Service
public class MortgageReportService extends BaseReportService<MortgageReport> {
    
    private final PropertyDataService propertyDataService;
    private final PropertyReportParser propertyReportParser;
    
    public MortgageReportService(
            PropertyDataService propertyDataService,
            PropertyReportParser propertyReportParser) {
        this.propertyDataService = propertyDataService;
        this.propertyReportParser = propertyReportParser;
    }
    
    @Override
    protected Map<String, Object> callService(DecisionRequest decisionRequest) {
        Address address = decisionRequest.getPrimaryAddress();
        return propertyDataService.call(address.getStreet());
    }
    
    @Override
    protected Map<String, Object> parseData(Map<String, Object> serviceData) {
        return propertyReportParser.parseToMap(serviceData);
    }
    
    @Override
    protected MortgageReport createReport(DecisionRequest decisionRequest, Map<String, Object> parsedData) {
        Address address = decisionRequest.getPrimaryAddress();
        return propertyReportParser.parse(parsedData, address);
    }
    
    @Override
    protected boolean saveReport(DecisionRequest decisionRequest, MortgageReport report) {
        if (report == null) return false;
        
        Address address = decisionRequest.getPrimaryAddress();
        if (address != null) {
            address.getMortgageReports().add(report);
            return true;
        }
        return false;
    }
}