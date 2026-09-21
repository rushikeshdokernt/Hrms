package com.gateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
 
import java.util.Arrays;

@Configuration
public class CorsConfig {
 
    @Value("${cors.allowedOrigins:#{null}}")
    private String[] allowedOrigins;
 
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Use patterns if allowedOrigins is set, otherwise use default pattern
        if (allowedOrigins != null && allowedOrigins.length > 0 && !allowedOrigins[0].equals("*")) {
            config.setAllowedOrigins(Arrays.asList(allowedOrigins));
            config.setAllowCredentials(true);
        } else {
            // ✅ Use patterns for wildcard
            config.setAllowedOriginPatterns(Arrays.asList("*"));
            config.setAllowCredentials(true);
        }

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}

