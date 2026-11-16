package com.placementhub.controller;

import com.placementhub.dto.PageResult;
import com.placementhub.dto.ProfileForm;
import com.placementhub.dto.SessionUser;
import com.placementhub.entity.Job;
import com.placementhub.entity.Student;
import com.placementhub.service.ServiceException;
import com.placementhub.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/student")
public class StudentController {

    private static final int PAGE_SIZE = 5;

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    private Long studentId(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
        return user.getProfileId();
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long sid = studentId(session);
        Student profile = studentService.getProfile(sid);
        model.addAttribute("profile", profile);
        model.addAttribute("eligibleJobs", studentService.eligibleJobs(sid));
        model.addAttribute("applications", studentService.myApplications(sid));
        return "student/dashboard";
    }

    @GetMapping("/jobs")
    public String jobs(HttpSession session, Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) BigDecimal minCgpa,
                       @RequestParam(defaultValue = "1") int page) {
        PageResult<Job> result = studentService.searchJobs(keyword, location, minCgpa, page, PAGE_SIZE);
        model.addAttribute("result", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("minCgpa", minCgpa);
        return "student/jobs";
    }

    @PostMapping("/jobs/{id}/apply")
    public String apply(HttpSession session, @PathVariable("id") Long jobId,
                        RedirectAttributes ra) {
        try {
            studentService.apply(studentId(session), jobId);
            ra.addFlashAttribute("success", "Application submitted successfully.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/student/jobs";
    }

    @GetMapping("/applications")
    public String applications(HttpSession session, Model model) {
        model.addAttribute("applications", studentService.myApplications(studentId(session)));
        return "student/applications";
    }

    @GetMapping("/profile")
    public String profileForm(HttpSession session, Model model) {
        Student profile = studentService.getProfile(studentId(session));
        if (!model.containsAttribute("profileForm")) {
            ProfileForm form = new ProfileForm();
            form.setBranch(profile.getBranch());
            form.setCgpa(profile.getCgpa());
            form.setSkills(profile.getSkills());
            form.setGradYear(profile.getGradYear());
            model.addAttribute("profileForm", form);
        }
        model.addAttribute("profile", profile);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(HttpSession session,
                                @Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult binding, Model model, RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("profile", studentService.getProfile(studentId(session)));
            return "student/profile";
        }
        studentService.updateProfile(studentId(session), form);
        ra.addFlashAttribute("success", "Profile updated.");
        return "redirect:/student/profile";
    }
}
