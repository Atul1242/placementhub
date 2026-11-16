package com.placementhub.service;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.dao.JobDao;
import com.placementhub.dao.StudentDao;
import com.placementhub.dto.PageResult;
import com.placementhub.dto.ProfileForm;
import com.placementhub.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentDao studentDao;
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;

    public StudentService(StudentDao studentDao, JobDao jobDao, ApplicationDao applicationDao) {
        this.studentDao = studentDao;
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
    }

    @Transactional(readOnly = true)
    public Student getProfile(Long studentId) {
        return studentDao.findById(studentId)
                .orElseThrow(() -> new ServiceException("Student profile not found."));
    }

    public Student updateProfile(Long studentId, ProfileForm form) {
        Student student = getProfile(studentId);
        student.setBranch(form.getBranch());
        student.setCgpa(form.getCgpa());
        student.setSkills(form.getSkills());
        student.setGradYear(form.getGradYear());
        return studentDao.update(student);
    }

    @Transactional(readOnly = true)
    public PageResult<Job> searchJobs(String keyword, String location, BigDecimal minCgpa, int page, int pageSize) {
        return jobDao.search(keyword, location, minCgpa, page, pageSize);
    }

    @Transactional(readOnly = true)
    public List<Job> eligibleJobs(Long studentId) {
        Student student = getProfile(studentId);
        BigDecimal cgpa = student.getCgpa() == null ? BigDecimal.ZERO : student.getCgpa();
        return jobDao.findEligibleForStudent(cgpa);
    }

    @Transactional(readOnly = true)
    public List<Application> myApplications(Long studentId) {
        return applicationDao.findByStudent(studentId);
    }

    /**
     * Apply to a job. Enforces: job exists &amp; open, deadline not passed,
     * company approved, CGPA eligibility, and no duplicate application.
     */
    public Application apply(Long studentId, Long jobId) {
        Student student = getProfile(studentId);
        Job job = jobDao.findById(jobId)
                .orElseThrow(() -> new ServiceException("Job not found."));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ServiceException("This job is no longer open for applications.");
        }
        if (!job.getCompany().isApproved()) {
            throw new ServiceException("This posting is not available.");
        }
        if (job.getDeadline() != null && job.getDeadline().isBefore(LocalDate.now())) {
            throw new ServiceException("The application deadline has passed.");
        }
        if (job.getMinCgpa() != null) {
            if (student.getCgpa() == null || student.getCgpa().compareTo(job.getMinCgpa()) < 0) {
                throw new ServiceException("Your CGPA does not meet the minimum of " + job.getMinCgpa() + ".");
            }
        }
        if (applicationDao.existsByStudentAndJob(studentId, jobId)) {
            throw new ServiceException("You have already applied to this job.");
        }

        return applicationDao.save(new Application(student, job));
    }
}
