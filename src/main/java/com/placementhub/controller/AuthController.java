package com.placementhub.controller;

import com.placementhub.dto.LoginForm;
import com.placementhub.dto.RegisterForm;
import com.placementhub.dto.SessionUser;
import com.placementhub.entity.Role;
import com.placementhub.service.AuthService;
import com.placementhub.service.ServiceException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    static String dashboardFor(Role role) {
        return switch (role) {
            case STUDENT -> "redirect:/student/dashboard";
            case RECRUITER -> "redirect:/recruiter/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);
        if (user != null) {
            return dashboardFor(user.getRole());
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(Model model,
                            @RequestParam(required = false) String required,
                            @RequestParam(required = false) String denied) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        if (required != null) {
            model.addAttribute("info", "Please log in to continue.");
        }
        if (denied != null) {
            model.addAttribute("error", "You do not have access to that page.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form,
                        BindingResult binding, HttpSession session, Model model) {
        if (binding.hasErrors()) {
            return "login";
        }
        try {
            SessionUser user = authService.login(form.getEmail(), form.getPassword());
            session.setAttribute(SessionUser.SESSION_KEY, user);
            return dashboardFor(user.getRole());
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult binding, HttpSession session, Model model) {
        if (binding.hasErrors()) {
            return "register";
        }
        try {
            SessionUser user = authService.register(form);
            session.setAttribute(SessionUser.SESSION_KEY, user);
            return dashboardFor(user.getRole());
        } catch (ServiceException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
