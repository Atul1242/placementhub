package com.placementhub.controller;

import com.placementhub.service.AdminService;
import com.placementhub.service.ServiceException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pending", adminService.pendingCompanies());
        model.addAttribute("companies", adminService.allCompanies());
        model.addAttribute("totalCompanies", adminService.totalCompanies());
        model.addAttribute("pendingCount", adminService.pendingCompanyCount());
        model.addAttribute("applicantsPerJob", adminService.applicantsPerJob());
        return "admin/dashboard";
    }

    @PostMapping("/companies/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        try {
            adminService.approveCompany(id);
            ra.addFlashAttribute("success", "Recruiter approved.");
        } catch (ServiceException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
