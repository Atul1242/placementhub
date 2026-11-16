package com.placementhub.service;

import com.placementhub.dao.*;
import com.placementhub.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds a demo admin, a couple of recruiters with jobs, and a sample student the
 * first time the app starts against an empty database. Idempotent: it checks for
 * the admin account and does nothing if it already exists.
 */
@Component
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserDao userDao;
    private final StudentDao studentDao;
    private final CompanyDao companyDao;
    private final JobDao jobDao;
    private final ApplicationDao applicationDao;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserDao userDao, StudentDao studentDao, CompanyDao companyDao,
                      JobDao jobDao, ApplicationDao applicationDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.studentDao = studentDao;
        this.companyDao = companyDao;
        this.jobDao = jobDao;
        this.applicationDao = applicationDao;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    @Transactional
    public void seed(ContextRefreshedEvent event) {
        // Only seed from the root context and only once.
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        if (userDao.existsByEmail("admin@placementhub.test")) {
            return;
        }
        log.info("Seeding demo data...");

        // Admin
        userDao.save(new User("Platform Admin", "admin@placementhub.test",
                passwordEncoder.encode("admin123"), Role.ADMIN));

        // Recruiter 1 (approved) + jobs
        Company acme = recruiter("Priya Nair", "priya@acme.test", "Acme Corp",
                "https://acme.test", true);
        job(acme, "Java Backend Intern",
                "Work on Spring MVC services and Hibernate persistence. Great for CS/IT students.",
                new BigDecimal("7.00"), "Bengaluru", LocalDate.now().plusDays(30));
        job(acme, "Frontend Engineer Intern",
                "Build React dashboards. Open to all branches with strong fundamentals.",
                new BigDecimal("6.50"), "Remote", LocalDate.now().plusDays(20));

        // Recruiter 2 (approved) + job
        Company globex = recruiter("Rahul Sen", "rahul@globex.test", "Globex Ltd",
                "https://globex.test", true);
        Job dataJob = job(globex, "Data Analyst Intern",
                "SQL, dashboards and reporting. Analytical mindset required.",
                new BigDecimal("8.00"), "Hyderabad", LocalDate.now().plusDays(25));

        // Recruiter 3 (pending approval, so admin has something to approve)
        recruiter("Meena Rao", "meena@initech.test", "Initech",
                "https://initech.test", false);

        // Sample student + one application
        Student student = student("Atul Gupta", "student@placementhub.test",
                "Computer Science", new BigDecimal("8.20"), "Java, Spring, SQL, Git", 2026);
        applicationDao.save(new Application(student, dataJob));

        log.info("Demo data seeded. Try admin@placementhub.test / student@placementhub.test (password: admin123 / student123).");
    }

    private Company recruiter(String name, String email, String companyName, String website, boolean approved) {
        User user = userDao.save(new User(name, email,
                passwordEncoder.encode("recruiter123"), Role.RECRUITER));
        Company company = new Company();
        company.setUser(user);
        company.setName(companyName);
        company.setWebsite(website);
        company.setApproved(approved);
        return companyDao.save(company);
    }

    private Student student(String name, String email, String branch, BigDecimal cgpa, String skills, int gradYear) {
        User user = userDao.save(new User(name, email,
                passwordEncoder.encode("student123"), Role.STUDENT));
        Student student = new Student();
        student.setUser(user);
        student.setBranch(branch);
        student.setCgpa(cgpa);
        student.setSkills(skills);
        student.setGradYear(gradYear);
        return studentDao.save(student);
    }

    private Job job(Company company, String title, String description, BigDecimal minCgpa,
                    String location, LocalDate deadline) {
        Job job = new Job();
        job.setCompany(company);
        job.setTitle(title);
        job.setDescription(description);
        job.setMinCgpa(minCgpa);
        job.setLocation(location);
        job.setDeadline(deadline);
        job.setStatus(JobStatus.OPEN);
        return jobDao.save(job);
    }
}
