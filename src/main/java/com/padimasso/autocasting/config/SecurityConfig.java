package com.padimasso.autocasting.config;

import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.security.filter.JwtAuthenticationFilter;
import com.padimasso.autocasting.application.auth.service.*;
import com.padimasso.autocasting.application.legal.security.LegalAcceptanceEnforcementFilter;
import com.padimasso.autocasting.application.legal.service.LegalService;
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
    private final LegalAcceptanceEnforcementFilter legalAcceptanceEnforcementFilter;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final LegalService legalService;

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
                    // Public Auth Endpoints
                    .requestMatchers(HttpMethod.POST, AppConstants.REGISTER_API_URL).permitAll()
                    .requestMatchers(HttpMethod.POST, AppConstants.LOGIN_API_URL).permitAll()
                    .requestMatchers(HttpMethod.POST, AppConstants.ADMIN_LOGIN_API_URL).permitAll()
                    .requestMatchers(HttpMethod.POST, AppConstants.FORGOT_PASS_URL).permitAll()
                    .requestMatchers(HttpMethod.POST, AppConstants.RESET_PASS_URL).permitAll()
                    // Public Content Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.SITE_METADATA_URL).permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.SITE_METADATA_VERSION_URL).permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.LEGAL_CURRENT_DOCUMENT_API_URL).permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.TALENT_PROFILE_API_URL + "/*").permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.CASTING_DATABASE_API_URL).permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.TALENT_DATABASE_API_URL).permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.CASTING_DETAILS_URL + "/*/roles/*").permitAll()
                    .requestMatchers(HttpMethod.GET, AppConstants.CASTING_DETAILS_URL + "/*").permitAll()
                    // Authenticated Shared Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.ME_API_URL).authenticated()
                    .requestMatchers(HttpMethod.PATCH, AppConstants.ONBOARDING_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.CHANGE_PASS_URL).authenticated()
                    .requestMatchers(HttpMethod.GET, AppConstants.LEGAL_REQUIREMENTS_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.LEGAL_ACCEPT_DOCUMENT_API_URL).authenticated()
                    .requestMatchers(HttpMethod.POST, AppConstants.LEGAL_ACCEPT_CURRENT_API_URL).authenticated()
                    // Test Endpoints
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_CASTINERA_API_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.GET, AppConstants.TEST_ACTOR_API_URL).hasRole(TALENT.name())
                    // Admin
                    .requestMatchers(AppConstants.ADMIN_API_URL + "/**").hasRole("ADMIN")
                    // Talent
                    .requestMatchers(HttpMethod.GET, AppConstants.TALENT_PROFILE_API_URL).hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/basic-info").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/contact").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/social-media").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/media").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/characteristics").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.TALENT_PROFILE_API_URL + "/skills").hasRole(TALENT.name())
                    .requestMatchers(AppConstants.CREDIT_API_URL + "/**").hasRole(TALENT.name())
                    .requestMatchers(AppConstants.EDUCATION_API_URL + "/**").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.POST, AppConstants.TALENT_CASTING_APPLICATION_URL + "/**").hasRole(TALENT.name())
                    .requestMatchers(HttpMethod.GET, AppConstants.TALENT_CASTING_APPLICATIONS_URL).hasRole(TALENT.name())
                    // Employer
                    .requestMatchers(HttpMethod.GET, AppConstants.EMPLOYER_PROFILE_API_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.EMPLOYER_PROFILE_API_URL + "/basic-info").hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.EMPLOYER_PROFILE_API_URL + "/social-media").hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.EMPLOYER_CASTINGS_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.EMPLOYER_CASTING_URL + "/**").hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.EMPLOYER_CASTING_APPLICATIONS_URL + "/**").hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.POST, AppConstants.CASTING_DETAILS_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.PATCH, AppConstants.CASTING_DETAILS_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(HttpMethod.DELETE, AppConstants.CASTING_DETAILS_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_BASIC_INFO_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_BASIC_INFO_URL + "/**").hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_ROLE_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_ROLE_URL + "/**").hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_REQUIREMENT_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_REQUIREMENT_URL + "/**").hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_REMUNERATION_URL).hasRole(EMPLOYER.name())
                    .requestMatchers(AppConstants.CASTING_REMUNERATION_URL + "/**").hasRole(EMPLOYER.name())
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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(legalAcceptanceEnforcementFilter, JwtAuthenticationFilter.class);

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
            legalService,
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
            "https://app-autocasting.vercel.app",
            "https://autocasting-admin.vercel.app")
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
