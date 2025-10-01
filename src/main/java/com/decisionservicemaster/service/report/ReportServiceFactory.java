package com.decisionservicemaster.service.report;

import com.decisionservicemaster.domain.entity.CreditReport;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.domain.entity.MortgageReport;
import org.springframework.stereotype.Component;

import com.decisionservicemaster.service.parser.ApplicantReportParser;
import com.decisionservicemaster.service.parser.PropertyReportParser;
import com.decisionservicemaster.service.report.CreditReportService;
import com.decisionservicemaster.service.report.MortgageReportService;

@Component
public class ReportServiceFactory {
    
    private final ApplicantDataService applicantDataService;
    private final PropertyDataService propertyDataService;
    private final ApplicantReportParser applicantReportParser;
    private final PropertyReportParser propertyReportParser;
    
    public ReportServiceFactory(
            ApplicantDataService applicantDataService,
            PropertyDataService propertyDataService,
            ApplicantReportParser applicantReportParser,
            PropertyReportParser propertyReportParser) {
        this.applicantDataService = applicantDataService;
        this.propertyDataService = propertyDataService;
        this.applicantReportParser = applicantReportParser;
        this.propertyReportParser = propertyReportParser;
    }
    
    public CreditReport fetchCreditReport(DecisionRequest decisionRequest) {
        CreditReportService service = new CreditReportService(applicantDataService, applicantReportParser);
        return service.fetch(decisionRequest);
    }
    
    public MortgageReport fetchMortgageReport(DecisionRequest decisionRequest) {
        MortgageReportService service = new MortgageReportService(propertyDataService, propertyReportParser);
        return service.fetch(decisionRequest);
    }
}