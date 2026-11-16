package com.placementhub.service;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.dao.CompanyDao;
import com.placementhub.dao.JobDao;
import com.placementhub.dto.JobForm;
import com.placementhub.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecruiterService {

    private final CompanyDao companyDao;
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;

    public RecruiterService(CompanyDao companyDao, JobDao jobDao, ApplicationDao applicationDao) {
        this.companyDao = companyDao;
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
    }

    @Transactional(readOnly = true)
    public Company getCompany(Long companyId) {
        return companyDao.findById(companyId)
                .orElseThrow(() -> new ServiceException("Company profile not found."));
    }

    @Transactional(readOnly = true)
    public List<Job> myJobs(Long companyId) {
        return jobDao.findByCompany(companyId);
    }

    public Job postJob(Long companyId, JobForm form) {
        Company company = getCompany(companyId);
        if (!company.isApproved()) {
            throw new ServiceException("Your company must be approved by an admin before posting jobs.");
        }
        Job job = new Job();
        job.setCompany(company);
        applyForm(job, form);
        job.setStatus(JobStatus.OPEN);
        return jobDao.save(job);
    }

    public Job updateJob(Long companyId, JobForm form) {
        Job job = loadOwnedJob(companyId, form.getId());
        applyForm(job, form);
        return jobDao.update(job);
    }

    public Job closeJob(Long companyId, Long jobId) {
        Job job = loadOwnedJob(companyId, jobId);
        job.setStatus(JobStatus.CLOSED);
        return jobDao.update(job);
    }

    @Transactional(readOnly = true)
    public List<Application> applicants(Long companyId, Long jobId) {
        loadOwnedJob(companyId, jobId);
        return applicationDao.findByJob(jobId);
    }

    /**
     * Shortlist / reject / select an applicant, enforcing valid status transitions
     * and company ownership of the underlying job.
     */
    public Application updateApplicationStatus(Long companyId, Long applicationId, ApplicationStatus target) {
        Application application = applicationDao.findById(applicationId)
                .orElseThrow(() -> new ServiceException("Application not found."));
        if (!application.getJob().getCompany().getId().equals(companyId)) {
            throw new ServiceException("You cannot modify an application for another company's job.");
        }
        if (!isValidTransition(application.getStatus(), target)) {
            throw new ServiceException("Cannot change status from "
                    + application.getStatus() + " to " + target + ".");
        }
        application.setStatus(target);
        return applicationDao.update(application);
    }

    static boolean isValidTransition(ApplicationStatus from, ApplicationStatus to) {
        return switch (from) {
            case APPLIED -> to == ApplicationStatus.SHORTLISTED || to == ApplicationStatus.REJECTED;
            case SHORTLISTED -> to == ApplicationStatus.SELECTED || to == ApplicationStatus.REJECTED;
            case REJECTED, SELECTED -> false; // terminal states
        };
    }

    private Job loadOwnedJob(Long companyId, Long jobId) {
        Job job = jobDao.findById(jobId)
                .orElseThrow(() -> new ServiceException("Job not found."));
        if (!job.getCompany().getId().equals(companyId)) {
            throw new ServiceException("You can only manage your own company's jobs.");
        }
        return job;
    }

    private void applyForm(Job job, JobForm form) {
        job.setTitle(form.getTitle());
        job.setDescription(form.getDescription());
        job.setMinCgpa(form.getMinCgpa());
        job.setLocation(form.getLocation());
        job.setDeadline(form.getDeadline());
    }
}
