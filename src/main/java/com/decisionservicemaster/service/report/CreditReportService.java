package com.decisionservicemaster.service.report;

import com.decisionservicemaster.domain.entity.Applicant;
import com.decisionservicemaster.domain.entity.CreditReport;
import com.decisionservicemaster.domain.entity.DecisionRequest;
import com.decisionservicemaster.service.parser.ApplicantReportParser;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class CreditReportService extends BaseReportService<CreditReport> {
    
    private final ApplicantDataService applicantDataService;
    private final ApplicantReportParser applicantReportParser;
    
    public CreditReportService(
            ApplicantDataService applicantDataService,
            ApplicantReportParser applicantReportParser) {
        this.applicantDataService = applicantDataService;
        this.applicantReportParser = applicantReportParser;
    }
    
    @Override
    protected Map<String, Object> callService(DecisionRequest decisionRequest) {
        Applicant applicant = decisionRequest.getPrimaryApplicant();
        return applicantDataService.call(applicant.getEncryptedSsn());
    }
    
    @Override
    protected Map<String, Object> parseData(Map<String, Object> serviceData) {
        return applicantReportParser.parseToMap(serviceData);
    }
    
    @Override
    protected CreditReport createReport(DecisionRequest decisionRequest, Map<String, Object> parsedData) {
        Applicant applicant = decisionRequest.getPrimaryApplicant();
        return applicantReportParser.parse(parsedData, applicant);
    }
    
    @Override
    protected boolean saveReport(DecisionRequest decisionRequest, CreditReport report) {
        if (report == null) return false;
        
        Applicant applicant = decisionRequest.getPrimaryApplicant();
        if (applicant != null) {
            applicant.getCreditReports().add(report);
            return true;
        }
        return false;
    }
}