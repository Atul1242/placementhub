package com.placementhub.dao;

import com.placementhub.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {
    Company save(Company company);

    Company update(Company company);

    Optional<Company> findById(Long id);

    Optional<Company> findByUserId(Long userId);

    List<Company> findAll();

    List<Company> findPendingApproval();
}
