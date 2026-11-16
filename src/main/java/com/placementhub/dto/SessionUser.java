package com.placementhub.dto;

import com.placementhub.entity.Role;

import java.io.Serializable;

/**
 * Minimal authenticated-user snapshot stored in the HttpSession after login.
 */
public class SessionUser implements Serializable {

    /** HttpSession attribute key under which the logged-in user is stored. */
    public static final String SESSION_KEY = "currentUser";

    private final Long userId;
    private final String name;
    private final Role role;
    private final Long profileId; // studentId or companyId depending on role

    public SessionUser(Long userId, String name, Role role, Long profileId) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.profileId = profileId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public Long getProfileId() {
        return profileId;
    }
}
