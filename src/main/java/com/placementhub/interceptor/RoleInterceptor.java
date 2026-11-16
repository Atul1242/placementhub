package com.placementhub.interceptor;

import com.placementhub.dto.SessionUser;
import com.placementhub.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards /student/**, /recruiter/** and /admin/** URLs. Unauthenticated users
 * are sent to the login page; authenticated users whose role does not match the
 * URL section are rejected.
 */
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());

        HttpSession session = request.getSession(false);
        SessionUser user = session == null ? null
                : (SessionUser) session.getAttribute(SessionUser.SESSION_KEY);

        if (user == null) {
            response.sendRedirect(contextPath + "/login?required");
            return false;
        }

        Role required = null;
        if (path.startsWith("/student")) {
            required = Role.STUDENT;
        } else if (path.startsWith("/recruiter")) {
            required = Role.RECRUITER;
        } else if (path.startsWith("/admin")) {
            required = Role.ADMIN;
        }

        if (required != null && user.getRole() != required) {
            response.sendRedirect(contextPath + "/login?denied");
            return false;
        }
        return true;
    }
}
