package com.placementhub.dao.impl;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.entity.Application;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ApplicationDaoImpl implements ApplicationDao {

    private final SessionFactory sessionFactory;

    public ApplicationDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Application save(Application application) {
        sessionFactory.getCurrentSession().persist(application);
        return application;
    }

    @Override
    public Application update(Application application) {
        return sessionFactory.getCurrentSession().merge(application);
    }

    @Override
    public Optional<Application> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Application.class, id));
    }

    @Override
    public boolean existsByStudentAndJob(Long studentId, Long jobId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(a.id) FROM Application a " +
                        "WHERE a.student.id = :studentId AND a.job.id = :jobId", Long.class)
                .setParameter("studentId", studentId)
                .setParameter("jobId", jobId)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public List<Application> findByStudent(Long studentId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Application a WHERE a.student.id = :studentId " +
                        "ORDER BY a.appliedAt DESC", Application.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    public List<Application> findByJob(Long jobId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Application a WHERE a.job.id = :jobId " +
                        "ORDER BY a.appliedAt ASC", Application.class)
                .setParameter("jobId", jobId)
                .getResultList();
    }

    @Override
    public List<Object[]> countApplicantsPerJob() {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT a.job.id, COUNT(a.id) FROM Application a " +
                        "GROUP BY a.job.id", Object[].class)
                .getResultList();
    }
}
