package com.jobtracking.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Disable CSRF (JWT based)
                                .csrf(csrf -> csrf.disable())

                                // Disable default auth
                                .httpBasic(httpBasic -> httpBasic.disable())
                                .formLogin(form -> form.disable())

                                // Stateless session
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Authorization rules
                                .authorizeHttpRequests(auth -> auth

                                                // Preflight
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // Auth
                                                .requestMatchers("/auth/register", "/auth/login").permitAll()
                                                .requestMatchers("/auth/me").authenticated()
                                                .requestMatchers(HttpMethod.GET, "/organizations").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/jobs", "/jobs/**").permitAll() // Allow public job browsing
                                                .requestMatchers("/recruiter/jobs/**").hasRole("RECRUITER")
                                                .requestMatchers("/jobs/**").hasRole("RECRUITER")
                                                .requestMatchers("/organizations/**").hasRole("RECRUITER")
                                                .requestMatchers("/dashboard/**").authenticated()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/applications/job/**").hasRole("RECRUITER")
                                                .requestMatchers("/applications/manage/**").hasRole("RECRUITER")
                                                .requestMatchers("/applications/me").hasRole("JOB_SEEKER")
                                                .requestMatchers("/applications/my").hasRole("JOB_SEEKER")
                                                .requestMatchers("/applications/check/**").hasRole("JOB_SEEKER")
                                                .requestMatchers(HttpMethod.POST, "/applications/**")
                                                .hasRole("JOB_SEEKER") // Job seekers can apply for jobs
                                                .requestMatchers("/profile/jobseeker").hasRole("JOB_SEEKER")
                                                .requestMatchers("/profile/recruiter").hasRole("RECRUITER")
                                                .requestMatchers("/profile/**").authenticated()
                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/webjars/**")
                                                .permitAll()
                                                .requestMatchers("/actuator/health").permitAll()
                                                .anyRequest().authenticated()) // Everything else protected

                                // JWT filter
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of(
                                "http://localhost:5173", // Vite default
                                "http://localhost:3000",  // React default
                                "http://localhost:4173",  // Vite preview
                                "http://localhost:8080",  // Alternative port
                                "https://jobsync.vivekbhosale.in",
                                "http://jobsync.vivekbhosale.in"));

                config.setAllowedMethods(List.of(
                                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                config.setAllowedHeaders(List.of(
                                "Authorization",
                                "Content-Type",
                                "Accept"));

                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return source;
        }
}