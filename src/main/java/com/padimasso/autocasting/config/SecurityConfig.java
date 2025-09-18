package com.padimasso.autocasting.config;

import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.security.filter.JwtAuthenticationFilter;
import com.padimasso.autocasting.application.auth.service.*;
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
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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

    private static final String ROLE_CASTINERA = "CASTINERA";
    private static final String ROLE_ACTOR = "ACTOR";
    private final AppProperties appProperties;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ClientRegistrationRepository clientRegistrationRepository,
                                                   GoogleOidcUserService oidcSrv,
                                                   GoogleOauth2UserService oauthSrv) throws Exception {
        http.authorizeHttpRequests(
                request -> request
                    // Test Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_CASTINERA_API_URL).hasRole(ROLE_CASTINERA)
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_ACTOR_API_URL).hasRole(ROLE_ACTOR)
                    // Profile
                    .requestMatchers(HttpMethod.GET, AppConstants.PROFILE_API_URL).authenticated()
                    // Any
                    .anyRequest().permitAll())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .oauth2Login(o -> o
                .authorizationEndpoint(a -> a
                    .authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository)))
                .userInfoEndpoint(ui -> ui
                    .oidcUserService(oidcSrv)   // OIDC
                    .userService(oauthSrv))     // OAuth2
                .successHandler(customOAuth2SuccessHandler()))
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
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository repo) {
        return new CustomAuthorizationRequestResolver(repo);
    }

    @Bean
    public AuthenticationSuccessHandler customOAuth2SuccessHandler() {
        return new CustomOAuth2SuccessHandler(appProperties, jwtService, userRepository);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed frontend URLs (without wildcards)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:8080",
            "https://app-autocasting.vercel.app")
        );

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

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
