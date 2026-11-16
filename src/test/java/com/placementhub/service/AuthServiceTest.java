package com.placementhub.service;

import com.placementhub.dao.CompanyDao;
import com.placementhub.dao.StudentDao;
import com.placementhub.dao.UserDao;
import com.placementhub.dto.RegisterForm;
import com.placementhub.dto.SessionUser;
import com.placementhub.entity.Company;
import com.placementhub.entity.Role;
import com.placementhub.entity.Student;
import com.placementhub.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserDao userDao;
    @Mock StudentDao studentDao;
    @Mock CompanyDao companyDao;
    // A real encoder so hashing/verification behaviour is exercised end-to-end.
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    AuthService authService;

    private AuthService service() {
        if (authService == null) {
            authService = new AuthService(userDao, studentDao, companyDao, passwordEncoder);
        }
        return authService;
    }

    @Test
    void register_throws_whenEmailExists() {
        when(userDao.existsByEmail("dup@test.com")).thenReturn(true);

        RegisterForm form = new RegisterForm();
        form.setName("Dup");
        form.setEmail("dup@test.com");
        form.setPassword("secret1");
        form.setRole("STUDENT");

        assertThrows(ServiceException.class, () -> service().register(form));
        verify(userDao, never()).save(any());
    }

    @Test
    void register_student_persistsUserAndProfile_withHashedPassword() {
        when(userDao.existsByEmail(anyString())).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(studentDao.save(any(Student.class))).thenAnswer(i -> {
            Student s = i.getArgument(0);
            s.setId(7L);
            return s;
        });

        RegisterForm form = new RegisterForm();
        form.setName("Atul");
        form.setEmail("atul@test.com");
        form.setPassword("secret1");
        form.setRole("STUDENT");
        form.setBranch("CSE");

        SessionUser session = service().register(form);

        assertEquals(Role.STUDENT, session.getRole());
        assertEquals(7L, session.getProfileId());
        verify(userDao).save(argThat(u -> !"secret1".equals(u.getPasswordHash())
                && passwordEncoder.matches("secret1", u.getPasswordHash())));
    }

    @Test
    void register_recruiter_throws_whenCompanyNameMissing() {
        when(userDao.existsByEmail(anyString())).thenReturn(false);
        when(userDao.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        RegisterForm form = new RegisterForm();
        form.setName("Rec");
        form.setEmail("rec@test.com");
        form.setPassword("secret1");
        form.setRole("RECRUITER");
        // no company name

        assertThrows(ServiceException.class, () -> service().register(form));
    }

    @Test
    void login_succeeds_withCorrectPassword() {
        User user = new User("Atul", "atul@test.com",
                passwordEncoder.encode("secret1"), Role.STUDENT);
        user.setId(1L);
        Student student = new Student();
        student.setId(7L);
        when(userDao.findByEmail("atul@test.com")).thenReturn(Optional.of(user));
        when(studentDao.findByUserId(1L)).thenReturn(Optional.of(student));

        SessionUser session = service().login("atul@test.com", "secret1");

        assertEquals(Role.STUDENT, session.getRole());
        assertEquals(7L, session.getProfileId());
    }

    @Test
    void login_throws_withWrongPassword() {
        User user = new User("Atul", "atul@test.com",
                passwordEncoder.encode("secret1"), Role.STUDENT);
        user.setId(1L);
        when(userDao.findByEmail("atul@test.com")).thenReturn(Optional.of(user));

        assertThrows(ServiceException.class, () -> service().login("atul@test.com", "wrong"));
    }

    @Test
    void login_throws_whenUserNotFound() {
        when(userDao.findByEmail("missing@test.com")).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, () -> service().login("missing@test.com", "secret1"));
    }
}
