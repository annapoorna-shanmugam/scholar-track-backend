package com.scholartrack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Normally the frontend is served by this same app (src/main/resources/static),
 * so no cross-origin request ever happens. This stays permissive for /api/**
 * only as a safety net in case the HTML is ever opened directly from disk or
 * from a separate dev server during development.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("*").allowedMethods("GET", "POST", "DELETE");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**");
    }
}
