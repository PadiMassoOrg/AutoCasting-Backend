package com.padimasso.autocasting.config;

import com.padimasso.autocasting.auth.model.Role;
import com.padimasso.autocasting.auth.repository.UserRepository;
import com.padimasso.autocasting.auth.security.filter.JwtAuthenticationFilter;
import com.padimasso.autocasting.auth.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final CustomOidcUserService customOidcUserService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                request -> request
                    // Test Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_ADMIN_API_URL)
                    .hasRole(String.valueOf(Role.CASTINERA))
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_USER_API_URL)
                    .hasRole(String.valueOf(Role.ACTOR))
                    // Any
                    .anyRequest().permitAll())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint
                    .authorizationRequestResolver(customAuthorizationRequestResolver)
                )
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOidcUserService)
                    .userService(customOAuth2UserService)
                )
                .successHandler(customOAuth2SuccessHandler)
                .failureHandler(customOAuth2FailureHandler)
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed frontend URLs (without wildcards)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow credentials (important for authentication)
        configuration.setAllowCredentials(true);

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // Expose headers to frontend (important if using Authorization header)
        configuration.setExposedHeaders(List.of("Authorization"));

        // Set cache duration
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
