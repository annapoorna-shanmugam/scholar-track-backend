package com.scholartrack.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Reads the signed-in user id out of the session. Only ever called from
 * behind SessionAuthInterceptor, which has already guaranteed it is present.
 */
final class CurrentUser {
    private CurrentUser() {
    }

    static Long id(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (Long) session.getAttribute(AuthController.SESSION_USER_ID);
    }
}
