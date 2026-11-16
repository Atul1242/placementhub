package com.placementhub.service;

import com.placementhub.dao.ApplicationDao;
import com.placementhub.dao.CompanyDao;
import com.placementhub.dao.JobDao;
import com.placementhub.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterServiceTest {

    @Mock CompanyDao companyDao;
    @Mock JobDao jobDao;
    @Mock ApplicationDao applicationDao;

    @InjectMocks RecruiterService recruiterService;

    @Test
    void transition_fromApplied_allowsShortlistAndReject() {
        assertTrue(RecruiterService.isValidTransition(ApplicationStatus.APPLIED, ApplicationStatus.SHORTLISTED));
        assertTrue(RecruiterService.isValidTransition(ApplicationStatus.APPLIED, ApplicationStatus.REJECTED));
        assertFalse(RecruiterService.isValidTransition(ApplicationStatus.APPLIED, ApplicationStatus.SELECTED));
    }

    @Test
    void transition_fromShortlisted_allowsSelectAndReject() {
        assertTrue(RecruiterService.isValidTransition(ApplicationStatus.SHORTLISTED, ApplicationStatus.SELECTED));
        assertTrue(RecruiterService.isValidTransition(ApplicationStatus.SHORTLISTED, ApplicationStatus.REJECTED));
    }

    @Test
    void transition_fromTerminalStates_isRejected() {
        assertFalse(RecruiterService.isValidTransition(ApplicationStatus.SELECTED, ApplicationStatus.REJECTED));
        assertFalse(RecruiterService.isValidTransition(ApplicationStatus.REJECTED, ApplicationStatus.SELECTED));
    }

    @Test
    void postJob_throws_whenCompanyNotApproved() {
        Company company = new Company();
        company.setId(5L);
        company.setApproved(false);
        when(companyDao.findById(5L)).thenReturn(Optional.of(company));

        com.placementhub.dto.JobForm form = new com.placementhub.dto.JobForm();
        form.setTitle("X");
        form.setDescription("Y");

        assertThrows(ServiceException.class, () -> recruiterService.postJob(5L, form));
        verify(jobDao, never()).save(any());
    }

    @Test
    void updateApplicationStatus_throws_whenJobBelongsToAnotherCompany() {
        Company owner = new Company();
        owner.setId(9L);
        Job job = new Job();
        job.setId(100L);
        job.setCompany(owner);
        Application application = new Application();
        application.setId(50L);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);
        when(applicationDao.findById(50L)).thenReturn(Optional.of(application));

        // caller company id 5 != owner 9
        assertThrows(ServiceException.class,
                () -> recruiterService.updateApplicationStatus(5L, 50L, ApplicationStatus.SHORTLISTED));
    }

    @Test
    void updateApplicationStatus_rejectsInvalidTransition() {
        Company owner = new Company();
        owner.setId(5L);
        Job job = new Job();
        job.setId(100L);
        job.setCompany(owner);
        Application application = new Application();
        application.setId(50L);
        application.setJob(job);
        application.setStatus(ApplicationStatus.APPLIED);
        when(applicationDao.findById(50L)).thenReturn(Optional.of(application));

        // APPLIED -> SELECTED is not allowed
        assertThrows(ServiceException.class,
                () -> recruiterService.updateApplicationStatus(5L, 50L, ApplicationStatus.SELECTED));
    }
}
