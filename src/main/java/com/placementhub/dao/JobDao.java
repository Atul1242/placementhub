package com.placementhub.dao;

import com.placementhub.dto.PageResult;
import com.placementhub.entity.Job;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface JobDao {
    Job save(Job job);

    Job update(Job job);

    Optional<Job> findById(Long id);

    List<Job> findByCompany(Long companyId);

    /**
     * Keyword + location + min-CGPA search over OPEN jobs from approved companies,
     * with pagination.
     */
    PageResult<Job> search(String keyword, String location, BigDecimal maxMinCgpa, int page, int pageSize);

    /**
     * HQL query: all OPEN jobs a student with the given CGPA is eligible for
     * (min_cgpa &lt;= cgpa and not past the deadline), from approved companies.
     */
    List<Job> findEligibleForStudent(BigDecimal studentCgpa);
}
