package com.lankaid.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // 1. PUBLIC AREA: Home page, NIC check, static files (css/js)
                        .requestMatchers("/", "/index.html", "/check-nic", "/logo.png").permitAll()
                        // 2. RESTRICTED AREA: Everything else (Dashboard, Delete, API lists)
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        // This enables the default login page
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .csrf(csrf -> csrf.disable()); // Disable CSRF for easier testing (not for production)

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 3. CREATE THE ADMIN USER (In Memory)
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("1234") // The password
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}

