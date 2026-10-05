package com.scholartrack.config;

import com.scholartrack.controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/** Blocks every /api/** call (except /api/auth/**, wired up in WebConfig) unless the request has a signed-in session. */
public class SessionAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AuthController.SESSION_USER_ID) != null) {
            return true;
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Sign in required.");
        return false;
    }
}
