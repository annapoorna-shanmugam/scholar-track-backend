package com.scholartrack.controller;

import com.scholartrack.dto.ProgressResponse;
import com.scholartrack.service.ProgressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public ProgressResponse progress(HttpServletRequest request) {
        return progressService.build(CurrentUser.id(request));
    }
}
