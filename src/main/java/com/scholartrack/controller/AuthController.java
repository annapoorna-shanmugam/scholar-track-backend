package com.scholartrack.controller;

import com.scholartrack.dto.AuthRequest;
import com.scholartrack.dto.UserResponse;
import com.scholartrack.model.User;
import com.scholartrack.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Session-based auth: a successful register/login stores the user id in the
 * HttpSession (cookie JSESSIONID), and SessionAuthInterceptor checks that
 * same session for every other /api/** call.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public static final String SESSION_USER_ID = "userId";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody AuthRequest req, HttpServletRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That username is already taken.");
        }
        User user = new User(req.username(), passwordEncoder.encode(req.password()));
        user = userRepository.save(user);
        request.getSession(true).setAttribute(SESSION_USER_ID, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user.getId(), user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody AuthRequest req, HttpServletRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(req.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password."));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password.");
        }
        request.getSession(true).setAttribute(SESSION_USER_ID, user.getId());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = session == null ? null : (Long) session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not signed in.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not signed in."));
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername()));
    }
}
