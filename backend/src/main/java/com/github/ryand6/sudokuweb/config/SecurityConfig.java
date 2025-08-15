package com.github.ryand6.sudokuweb.config;

import com.github.ryand6.sudokuweb.security.OAuth2SuccessHandler;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf((csrf) -> csrf
                        // Send the csrf token as a cookie that is readable by React so that it can attach this to HTTP request headers
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                // Configure when authorization is required
                .authorizeHttpRequests(auth -> auth
                        // Require that any URL requires authentication except for homepage, css files, and user set-up
                        .requestMatchers("/", "/error/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Enable OAuth2 login
                .oauth2Login(oauth -> oauth
                        .successHandler(new OAuth2SuccessHandler(userService))
                )

                // Add logout config
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Where to go after logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID") // Default name of cookie used by Java servlet containers (e.g. Tomcat, Jetty)
                );

                // Handle access errors gracefully
                //.exceptionHandling(ex -> ex
                //        .accessDeniedPage("/access-denied") // Custom Access Denied page
                //);

        return http.build();
    }


}
