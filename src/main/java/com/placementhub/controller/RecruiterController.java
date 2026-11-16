package com.placementhub.controller;

import com.placementhub.dto.JobForm;
import com.placementhub.dto.SessionUser;
import com.placementhub.entity.ApplicationStatus;
import com.placementhub.entity.Company;
import com.placementhub.entity.Job;
import com.placementhub.service.RecruiterService;
import com.placementhub.service.ServiceException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recruiter")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    private Long companyId(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
        return user.getProfileId();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long cid = companyId(session);
        Company company = recruiterService.getCompany(cid);
        model.addAttribute("company", company);
        model.addAttribute("jobs", recruiterService.myJobs(cid));
        return "recruiter/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJob(Model model) {
        if (!model.containsAttribute("jobForm")) {
            model.addAttribute("jobForm", new JobForm());
        }
        model.addAttribute("mode", "new");
        return "recruiter/job-form";
    }

    @PostMapping("/jobs")
    public String create(HttpSession session, @Valid @ModelAttribute("jobForm") JobForm form,
                         BindingResult binding, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("mode", "new");
            return "recruiter/job-form";
        }
        try {
            recruiterService.postJob(companyId(session), form);
            ra.addFlashAttribute("success", "Job posted.");
            return "redirect:/recruiter/dashboard";
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("mode", "new");
            return "recruiter/job-form";
        }
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(HttpSession session, @PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Job job = loadJob(session, id);
            JobForm form = new JobForm();
            form.setId(job.getId());
            form.setTitle(job.getTitle());
            form.setDescription(job.getDescription());
            form.setMinCgpa(job.getMinCgpa());
            form.setLocation(job.getLocation());
            form.setDeadline(job.getDeadline());
            model.addAttribute("jobForm", form);
            model.addAttribute("mode", "edit");
            return "recruiter/job-form";
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/recruiter/dashboard";
        }
    }

    private Job loadJob(HttpSession session, Long id) {
        Long cid = companyId(session);
        return recruiterService.myJobs(cid).stream()
                .filter(j -> j.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Job not found."));
    }

    @PostMapping("/jobs/{id}")
    public String update(HttpSession session, @PathVariable Long id,
                         @Valid @ModelAttribute("jobForm") JobForm form,
                         BindingResult binding, Model model, RedirectAttributes ra) {
        form.setId(id);
        if (binding.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "recruiter/job-form";
        }
        try {
            recruiterService.updateJob(companyId(session), form);
            ra.addFlashAttribute("success", "Job updated.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/recruiter/dashboard";
    }

    @PostMapping("/jobs/{id}/close")
    public String close(HttpSession session, @PathVariable Long id, RedirectAttributes ra) {
        try {
            recruiterService.closeJob(companyId(session), id);
            ra.addFlashAttribute("success", "Job closed.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/recruiter/dashboard";
    }

    @GetMapping("/jobs/{id}/applicants")
    public String applicants(HttpSession session, @PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("job", loadJob(session, id));
            model.addAttribute("applications", recruiterService.applicants(companyId(session), id));
            return "recruiter/applicants";
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/recruiter/dashboard";
        }
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(HttpSession session, @PathVariable("id") Long applicationId,
                               @RequestParam String status, @RequestParam Long jobId,
                               RedirectAttributes ra) {
        try {
            recruiterService.updateApplicationStatus(companyId(session), applicationId,
                    ApplicationStatus.valueOf(status));
            ra.addFlashAttribute("success", "Applicant marked " + status + ".");
        } catch (ServiceException | IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/recruiter/jobs/" + jobId + "/applicants";
    }
}
