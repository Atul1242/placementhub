package com.placementhub.dao.impl;

import com.placementhub.dao.StudentDao;
import com.placementhub.entity.Student;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StudentDaoImpl implements StudentDao {

    private final SessionFactory sessionFactory;

    public StudentDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Student save(Student student) {
        sessionFactory.getCurrentSession().persist(student);
        return student;
    }

    @Override
    public Student update(Student student) {
        return sessionFactory.getCurrentSession().merge(student);
    }

    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Student.class, id));
    }

    @Override
    public Optional<Student> findByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Student s WHERE s.user.id = :userId", Student.class)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }
}
