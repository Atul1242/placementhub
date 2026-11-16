package com.placementhub.service;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.dao.CompanyDao;
import com.placementhub.dao.JobDao;
import com.placementhub.entity.Company;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final CompanyDao companyDao;
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;

    public AdminService(CompanyDao companyDao, JobDao jobDao, ApplicationDao applicationDao) {
        this.companyDao = companyDao;
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
    }

    @Transactional(readOnly = true)
    public List<Company> pendingCompanies() {
        return companyDao.findPendingApproval();
    }

    @Transactional(readOnly = true)
    public List<Company> allCompanies() {
        return companyDao.findAll();
    }

    public Company approveCompany(Long companyId) {
        Company company = companyDao.findById(companyId)
                .orElseThrow(() -> new ServiceException("Company not found."));
        company.setApproved(true);
        return companyDao.update(company);
    }

    /**
     * [jobId, applicantCount] rows, produced by an HQL GROUP BY aggregate.
     */
    @Transactional(readOnly = true)
    public List<Object[]> applicantsPerJob() {
        return applicationDao.countApplicantsPerJob();
    }

    @Transactional(readOnly = true)
    public long totalCompanies() {
        return allCompanies().size();
    }

    @Transactional(readOnly = true)
    public long pendingCompanyCount() {
        return pendingCompanies().size();
    }
}
