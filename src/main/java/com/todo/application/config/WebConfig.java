package com.todo.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // This allows your API to be called from other domains.
        // For a monolith, this is optional, but crucial if you ever split the frontend.
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080") // Only allow our own domain
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensures standard static paths work correctly
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}