package com.placementhub.service;

/**
 * Thrown for business-rule violations (duplicate email, duplicate application,
 * ineligible applicant, bad credentials, ...). Carries a user-facing message.
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
