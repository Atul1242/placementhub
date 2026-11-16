package com.placementhub.dao.impl;

import com.placementhub.dao.JobDao;
import com.placementhub.dto.PageResult;
import com.placementhub.entity.Job;
import com.placementhub.entity.JobStatus;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JobDaoImpl implements JobDao {

    private final SessionFactory sessionFactory;

    public JobDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Job save(Job job) {
        sessionFactory.getCurrentSession().persist(job);
        return job;
    }

    @Override
    public Job update(Job job) {
        return sessionFactory.getCurrentSession().merge(job);
    }

    @Override
    public Optional<Job> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Job.class, id));
    }

    @Override
    public List<Job> findByCompany(Long companyId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Job j WHERE j.company.id = :companyId ORDER BY j.id DESC", Job.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }

    @Override
    public PageResult<Job> search(String keyword, String location, BigDecimal maxMinCgpa, int page, int pageSize) {
        StringBuilder where = new StringBuilder(
                " WHERE j.status = :open AND j.company.approved = true" +
                " AND (j.deadline IS NULL OR j.deadline >= :today)");
        List<Object[]> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (LOWER(j.title) LIKE :kw OR LOWER(j.description) LIKE :kw)");
            params.add(new Object[]{"kw", "%" + keyword.toLowerCase().trim() + "%"});
        }
        if (location != null && !location.isBlank()) {
            where.append(" AND LOWER(j.location) LIKE :loc");
            params.add(new Object[]{"loc", "%" + location.toLowerCase().trim() + "%"});
        }
        if (maxMinCgpa != null) {
            // jobs whose required CGPA is at most what the user filtered for
            where.append(" AND (j.minCgpa IS NULL OR j.minCgpa <= :cgpa)");
            params.add(new Object[]{"cgpa", maxMinCgpa});
        }

        Query<Job> dataQuery = sessionFactory.getCurrentSession()
                .createQuery("FROM Job j" + where + " ORDER BY j.deadline ASC, j.id DESC", Job.class)
                .setParameter("open", JobStatus.OPEN)
                .setParameter("today", LocalDate.now());

        Query<Long> countQuery = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(j.id) FROM Job j" + where, Long.class)
                .setParameter("open", JobStatus.OPEN)
                .setParameter("today", LocalDate.now());

        for (Object[] p : params) {
            dataQuery.setParameter((String) p[0], p[1]);
            countQuery.setParameter((String) p[0], p[1]);
        }

        long total = countQuery.uniqueResult();
        int safePage = Math.max(1, page);
        dataQuery.setFirstResult((safePage - 1) * pageSize);
        dataQuery.setMaxResults(pageSize);

        return new PageResult<>(dataQuery.getResultList(), total, safePage, pageSize);
    }

    @Override
    public List<Job> findEligibleForStudent(BigDecimal studentCgpa) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "FROM Job j " +
                        "WHERE j.status = :open " +
                        "AND j.company.approved = true " +
                        "AND (j.deadline IS NULL OR j.deadline >= :today) " +
                        "AND (j.minCgpa IS NULL OR j.minCgpa <= :cgpa) " +
                        "ORDER BY j.deadline ASC", Job.class)
                .setParameter("open", JobStatus.OPEN)
                .setParameter("today", LocalDate.now())
                .setParameter("cgpa", studentCgpa)
                .getResultList();
    }
}
