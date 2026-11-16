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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final CompanyDao companyDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserDao userDao, StudentDao studentDao,
                       CompanyDao companyDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.companyDao = companyDao;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a user plus the role-specific profile. Email must be unique and
     * the password is stored only as a BCrypt hash.
     */
    public SessionUser register(RegisterForm form) {
        if (userDao.existsByEmail(form.getEmail().trim())) {
            throw new ServiceException("An account with that email already exists.");
        }

        Role role = Role.valueOf(form.getRole());
        User user = new User(form.getName().trim(), form.getEmail().trim(),
                passwordEncoder.encode(form.getPassword()), role);
        userDao.save(user);

        if (role == Role.STUDENT) {
            Student student = new Student();
            student.setUser(user);
            student.setBranch(form.getBranch());
            student.setCgpa(form.getCgpa());
            student.setSkills(form.getSkills());
            student.setGradYear(form.getGradYear());
            studentDao.save(student);
            return new SessionUser(user.getId(), user.getName(), role, student.getId());
        } else {
            if (form.getCompanyName() == null || form.getCompanyName().isBlank()) {
                throw new ServiceException("Company name is required for recruiter accounts.");
            }
            Company company = new Company();
            company.setUser(user);
            company.setName(form.getCompanyName().trim());
            company.setWebsite(form.getWebsite());
            company.setApproved(false); // admin must approve
            companyDao.save(company);
            return new SessionUser(user.getId(), user.getName(), role, company.getId());
        }
    }

    /**
     * Verifies credentials and returns the session snapshot, or throws on failure.
     */
    @Transactional(readOnly = true)
    public SessionUser login(String email, String rawPassword) {
        User user = userDao.findByEmail(email == null ? "" : email.trim())
                .orElseThrow(() -> new ServiceException("Invalid email or password."));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ServiceException("Invalid email or password.");
        }

        Long profileId = switch (user.getRole()) {
            case STUDENT -> studentDao.findByUserId(user.getId()).map(Student::getId).orElse(null);
            case RECRUITER -> companyDao.findByUserId(user.getId()).map(Company::getId).orElse(null);
            case ADMIN -> null;
        };
        return new SessionUser(user.getId(), user.getName(), user.getRole(), profileId);
    }
}
