package com.placementhub.service;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.dao.JobDao;
import com.placementhub.dao.StudentDao;
import com.placementhub.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock StudentDao studentDao;
    @Mock JobDao jobDao;
    @Mock ApplicationDao applicationDao;

    @InjectMocks StudentService studentService;

    private Student student;
    private Job job;

    @BeforeEach
    void setUp() {
        User studentUser = new User("Atul", "atul@test.com", "hash", Role.STUDENT);
        studentUser.setId(1L);
        student = new Student();
        student.setId(10L);
        student.setUser(studentUser);
        student.setCgpa(new BigDecimal("8.00"));

        Company company = new Company();
        company.setId(5L);
        company.setName("Acme");
        company.setApproved(true);

        job = new Job();
        job.setId(100L);
        job.setCompany(company);
        job.setTitle("Java Intern");
        job.setMinCgpa(new BigDecimal("7.00"));
        job.setStatus(JobStatus.OPEN);
        job.setDeadline(LocalDate.now().plusDays(10));
    }

    @Test
    void apply_succeeds_whenEligibleAndNotDuplicate() {
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.of(job));
        when(applicationDao.existsByStudentAndJob(10L, 100L)).thenReturn(false);
        when(applicationDao.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        Application result = studentService.apply(10L, 100L);

        assertNotNull(result);
        assertEquals(ApplicationStatus.APPLIED, result.getStatus());
        verify(applicationDao).save(any(Application.class));
    }

    @Test
    void apply_throws_whenDuplicateApplication() {
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.of(job));
        when(applicationDao.existsByStudentAndJob(10L, 100L)).thenReturn(true);

        ServiceException ex = assertThrows(ServiceException.class, () -> studentService.apply(10L, 100L));
        assertTrue(ex.getMessage().toLowerCase().contains("already applied"));
        verify(applicationDao, never()).save(any());
    }

    @Test
    void apply_throws_whenCgpaBelowMinimum() {
        student.setCgpa(new BigDecimal("6.00"));
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.of(job));

        ServiceException ex = assertThrows(ServiceException.class, () -> studentService.apply(10L, 100L));
        assertTrue(ex.getMessage().contains("CGPA"));
        verify(applicationDao, never()).save(any());
    }

    @Test
    void apply_throws_whenJobClosed() {
        job.setStatus(JobStatus.CLOSED);
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.of(job));

        assertThrows(ServiceException.class, () -> studentService.apply(10L, 100L));
        verify(applicationDao, never()).save(any());
    }

    @Test
    void apply_throws_whenDeadlinePassed() {
        job.setDeadline(LocalDate.now().minusDays(1));
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.of(job));

        ServiceException ex = assertThrows(ServiceException.class, () -> studentService.apply(10L, 100L));
        assertTrue(ex.getMessage().toLowerCase().contains("deadline"));
    }

    @Test
    void apply_throws_whenJobMissing() {
        when(studentDao.findById(10L)).thenReturn(Optional.of(student));
        when(jobDao.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> studentService.apply(10L, 100L));
    }
}
