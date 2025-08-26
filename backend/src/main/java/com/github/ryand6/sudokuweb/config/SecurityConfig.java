package com.github.ryand6.sudokuweb.config;

import com.github.ryand6.sudokuweb.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // get from application.properties
    private final String spaBaseUrl;

    public SecurityConfig(@Value("${spa.base-url}") String spaBaseUrl) {
        this.spaBaseUrl = spaBaseUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS
                .cors(Customizer.withDefaults())
                // Publish csrf token in a cookie that React SPA can read
                .csrf((csrf) -> csrf
                        // Send the csrf token as a cookie that is readable by React so that it can attach this to HTTP request headers
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))

                // Configure when authorization is required
                .authorizeHttpRequests(auth -> auth
                        // Require that any URL requires authentication except for homepage, css files, and user set-up
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Enable OAuth2 login
                .oauth2Login(oauth -> oauth
                        .successHandler(new OAuth2SuccessHandler(spaBaseUrl))
                )

                // Add logout config
                .logout(logout -> logout
                        // Return HTTP 200 status, let React handle redirect
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // Default name of cookie used by Java servlet containers (e.g. Tomcat, Jetty)
                );

        return http.build();
    }

    // Used by default in http.cors() config
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // React origin
        configuration.setAllowedOrigins(List.of(spaBaseUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Required for JSESSIONID
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applies configuration to all endpoints in the backend
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


}
