package com.indu.resumeanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                List<String> allowedOrigins = new ArrayList<>();
                allowedOrigins.add("http://localhost:3000");
                allowedOrigins.add("http://localhost:3001");
                allowedOrigins.add("http://localhost:3002");
                allowedOrigins.add("http://localhost:5173");
                
                String frontendUrl = System.getenv("FRONTEND_URL");
                if (frontendUrl != null && !frontendUrl.isEmpty()) {
                    allowedOrigins.add(frontendUrl);
                }

                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(new String[0]))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
