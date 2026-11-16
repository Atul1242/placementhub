package com.placementhub.dao;

import com.placementhub.entity.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationDao {
    Application save(Application application);

    Application update(Application application);

    Optional<Application> findById(Long id);

    boolean existsByStudentAndJob(Long studentId, Long jobId);

    List<Application> findByStudent(Long studentId);

    List<Application> findByJob(Long jobId);

    /**
     * HQL aggregate query: number of applicants per job, as [jobId, count] rows.
     */
    List<Object[]> countApplicantsPerJob();
}
