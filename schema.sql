-- ---------------------------------------------------------------------------
-- PlacementHub schema (MySQL 8)
-- Hibernate can create these automatically (hibernate.hbm2ddl.auto=update),
-- but this file documents the 5 tables and their relationships explicitly.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS placementhub
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE placementhub;

-- 1. users -------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- 2. students (1-1 users) ----------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    user_id   BIGINT NOT NULL,
    branch    VARCHAR(80),
    cgpa      DECIMAL(4,2),
    skills    VARCHAR(500),
    grad_year INT,
    PRIMARY KEY (id),
    CONSTRAINT uk_students_user UNIQUE (user_id),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 3. companies (1-1 users) ---------------------------------------------------
CREATE TABLE IF NOT EXISTS companies (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    user_id  BIGINT       NOT NULL,
    name     VARCHAR(120) NOT NULL,
    website  VARCHAR(200),
    approved BIT(1)       NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    CONSTRAINT uk_companies_user UNIQUE (user_id),
    CONSTRAINT fk_companies_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

-- 4. jobs (N-1 companies) ----------------------------------------------------
CREATE TABLE IF NOT EXISTS jobs (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    company_id  BIGINT       NOT NULL,
    title       VARCHAR(150) NOT NULL,
    description VARCHAR(4000),
    min_cgpa    DECIMAL(4,2),
    location    VARCHAR(100),
    deadline    DATE,
    status      VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_jobs_company FOREIGN KEY (company_id) REFERENCES companies (id)
) ENGINE=InnoDB;

-- 5. applications (N-1 students, N-1 jobs) -----------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    student_id BIGINT      NOT NULL,
    job_id     BIGINT      NOT NULL,
    status     VARCHAR(20) NOT NULL,
    applied_at DATETIME    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_application_student_job UNIQUE (student_id, job_id),
    CONSTRAINT fk_applications_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs (id)
) ENGINE=InnoDB;
