package com.nova.membership.config;

import com.nova.membership.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter,
            @Value("${app.cors.allowed-origins}") String allowedOrigins
    ) {
        this.jwtFilter = jwtFilter;

        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource())
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint((req, res, ex) ->
                                writeJson(
                                        res,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Please log in to continue."
                                )
                        )

                        .accessDeniedHandler((req, res, ex) ->
                                writeJson(
                                        res,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "You do not have access to this resource."
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Allow CORS preflight requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Public APIs
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/members/register",
                                "/api/health",
                                "/uploads/**",
                                "/error"
                        )
                        .permitAll()

                        // Music: anyone can list and stream (the <audio> tag cannot send a JWT header)
                        .requestMatchers(HttpMethod.GET, "/api/songs", "/api/songs/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/api/songs", "/api/songs/**")
                        .permitAll()

                        // Volunteer application form (no login needed)
                        .requestMatchers(HttpMethod.POST, "/api/volunteers/apply")
                        .permitAll()

                        // Upload / delete songs, review volunteers: admin members only
                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Everything else requires JWT authentication
                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /** BCrypt for passwords (used by MemberService). */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        /*
         * Example:
         * http://localhost:5173
         * http://localhost:5174
         *
         * These can also be supplied through CORS_ORIGINS environment variable.
         */
        config.setAllowedOriginPatterns(allowedOrigins);

        config.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        config.setAllowedHeaders(List.of("*"));

        // JWT Bearer token is used instead of cookies
        config.setAllowCredentials(false);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                config
        );

        return source;
    }

    private static void writeJson(
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                "{\"success\":false,\"message\":\""
                        + message
                        + "\",\"data\":null}"
        );
    }
}