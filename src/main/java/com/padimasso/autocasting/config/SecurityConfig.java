package com.padimasso.autocasting.config;

import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.security.filter.JwtAuthenticationFilter;
import com.padimasso.autocasting.application.auth.service.*;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.exception.ApiAccessDeniedHandler;
import com.padimasso.autocasting.exception.ApiAuthenticationFailureHandler;
import com.padimasso.autocasting.exception.ApiErrorFactory;
import com.padimasso.autocasting.exception.JwtAuthEntryPoint;
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

import static com.padimasso.autocasting.application.auth.model.UserMode.EMPLOYER;
import static com.padimasso.autocasting.application.auth.model.UserMode.TALENT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class SecurityConfig {

    private final AppProperties appProperties;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ClientRegistrationRepository clientRegistrationRepository,
                                                   GoogleOidcUserService oidcSrv,
                                                   GoogleOauth2UserService oauthSrv,
                                                   JwtAuthEntryPoint jwtAuthEntryPoint,
                                                   ApiAccessDeniedHandler apiAccessDeniedHandler,
                                                   ApiAuthenticationFailureHandler apiAuthenticationFailureHandler,
                                                   ApiErrorFactory apiErrorFactory) throws Exception {
        http.authorizeHttpRequests(
                request -> request
                    // Test Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_CASTINERA_API_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_ACTOR_API_URL).hasRole(TALENT.name())
                    // Admin
                    .requestMatchers(HttpMethod.GET, AppConstants.ADMIN_API_URL + "/**").hasRole("ADMIN")
                    // Talent
                    .requestMatchers(HttpMethod.POST, AppConstants.TALENT_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.TALENT_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.TALENT_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.CREDIT_API_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.CREDIT_API_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.CREDIT_API_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.CREDIT_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.EDUCATION_API_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.EDUCATION_API_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.EDUCATION_API_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.EDUCATION_API_URL).authenticated()
                    // Employer
                    .requestMatchers(HttpMethod.POST, AppConstants.EMPLOYER_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.EMPLOYER_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.EMPLOYER_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.EMPLOYER_PROFILE_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.EMPLOYER_CASTINGS_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.EMPLOYER_CASTINGS_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.EMPLOYER_CASTINGS_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.EMPLOYER_CASTINGS_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.CASTING_DETAILS_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.CASTING_DETAILS_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.CASTING_DETAILS_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.CASTING_BASIC_INFO_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.CASTING_BASIC_INFO_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.CASTING_BASIC_INFO_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.CASTING_ROLE_ROLES_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.CASTING_ROLE_ROLES_URL).authenticated()
                    .requestMatchers(HttpMethod.DELETE, AppConstants.CASTING_ROLE_ROLES_URL).authenticated()
                    //TODO: Add: CASTING_ROLE, CASTING_REQUIREMENT_URL, CASTING_REQUIREMENT_REQUIREMENTS_URL, CASTING_REMUNERATION_URL

                    // Serve
                    .requestMatchers("/email/**", "/css/**", "/js/**", "/images/**").permitAll()
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
                .successHandler(customOAuth2SuccessHandler(apiErrorFactory))
                .failureHandler(apiAuthenticationFailureHandler))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint)
                .accessDeniedHandler(apiAccessDeniedHandler))
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
    public AuthenticationSuccessHandler customOAuth2SuccessHandler(ApiErrorFactory apiErrorFactory) {
        return new CustomOAuth2SuccessHandler(
            appProperties,
            jwtService,
            userRepository,
            talentProfileRepository,
            employerProfileRepository,
            apiErrorFactory
        );
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed frontend URLs (without wildcards)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:8080",
            "https://autocasting.app",
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
