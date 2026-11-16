package com.placementhub.dao.impl;

import com.placementhub.dao.CompanyDao;
import com.placementhub.entity.Company;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompanyDaoImpl implements CompanyDao {

    private final SessionFactory sessionFactory;

    public CompanyDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Company save(Company company) {
        sessionFactory.getCurrentSession().persist(company);
        return company;
    }

    @Override
    public Company update(Company company) {
        return sessionFactory.getCurrentSession().merge(company);
    }

    @Override
    public Optional<Company> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Company.class, id));
    }

    @Override
    public Optional<Company> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Company c WHERE c.user.id = :userId", Company.class)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }

    @Override
    public List<Company> findAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Company c ORDER BY c.name", Company.class)
                .getResultList();
    }

    @Override
    public List<Company> findPendingApproval() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Company c WHERE c.approved = false ORDER BY c.id", Company.class)
                .getResultList();
    }
}
