package com.placementhub.dao;

import com.placementhub.entity.Student;

import java.util.Optional;

public interface StudentDao {
    Student save(Student student);

    Student update(Student student);

    Optional<Student> findById(Long id);

    Optional<Student> findByUserId(Long userId);
}
