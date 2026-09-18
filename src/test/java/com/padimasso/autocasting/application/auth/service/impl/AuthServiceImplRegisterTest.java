package com.padimasso.autocasting.application.auth.service.impl;

import com.padimasso.autocasting.application.auth.context.AuthContext;
import com.padimasso.autocasting.application.auth.dto.request.RegisterRequest;
import com.padimasso.autocasting.application.auth.dto.response.AuthResponse;
import com.padimasso.autocasting.application.auth.model.RoleEntity;
import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.auth.repository.RoleRepository;
import com.padimasso.autocasting.application.auth.repository.UserRepository;
import com.padimasso.autocasting.application.auth.service.EmailService;
import com.padimasso.autocasting.application.auth.service.GoogleIdTokenVerifierService;
import com.padimasso.autocasting.application.auth.service.JwtService;
import com.padimasso.autocasting.application.auth.service.RefreshTokenService;
import com.padimasso.autocasting.application.auth.service.UserProvisioningService;
import com.padimasso.autocasting.application.employer.model.EmployerBasicInfoEntity;
import com.padimasso.autocasting.application.employer.model.EmployerProfileEntity;
import com.padimasso.autocasting.application.employer.repository.EmployerBasicInfoRepository;
import com.padimasso.autocasting.application.employer.repository.EmployerProfileRepository;
import com.padimasso.autocasting.application.legal.service.LegalService;
import com.padimasso.autocasting.application.plan.model.PlanEntity;
import com.padimasso.autocasting.application.plan.repository.PlanRepository;
import com.padimasso.autocasting.application.talent.model.BasicInfoEntity;
import com.padimasso.autocasting.application.talent.model.CharacteristicsEntity;
import com.padimasso.autocasting.application.talent.model.ContactEntity;
import com.padimasso.autocasting.application.talent.model.MediaEntity;
import com.padimasso.autocasting.application.talent.model.TalentProfileEntity;
import com.padimasso.autocasting.application.talent.repository.BasicInfoRepository;
import com.padimasso.autocasting.application.talent.repository.CharacteristicsRepository;
import com.padimasso.autocasting.application.talent.repository.ContactRepository;
import com.padimasso.autocasting.application.talent.repository.MediaRepository;
import com.padimasso.autocasting.application.talent.repository.TalentProfileRepository;
import com.padimasso.autocasting.application.talent.service.TalentWelcomeEmailService;
import com.padimasso.autocasting.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.padimasso.autocasting.application.auth.model.UserMode.EMPLOYER;
import static com.padimasso.autocasting.application.auth.model.UserMode.TALENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplRegisterTest {

    @Mock
    private SpringTemplateEngine templateEngine;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private TalentProfileRepository talentProfileRepository;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private BasicInfoRepository basicInfoRepository;
    @Mock
    private EmployerBasicInfoRepository employerBasicInfoRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private CharacteristicsRepository characteristicsRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private EmailService emailService;
    @Mock
    private AppProperties appProperties;
    @Mock
    private MessageSource messageSource;
    @Mock
    private AuthContext authContext;
    @Mock
    private GoogleIdTokenVerifierService googleIdTokenVerifierService;
    @Mock
    private UserProvisioningService userProvisioningService;
    @Mock
    private LegalService legalService;
    @Mock
    private TalentWelcomeEmailService talentWelcomeEmailService;

    private AuthServiceImpl service;

    private static final String EMAIL = "new-employer@example.com";
    private static final String PASSWORD = "s3cret!!";

    @BeforeEach
    void setUp() {
        service = new AuthServiceImpl(
            templateEngine,
            userRepository,
            roleRepository,
            planRepository,
            talentProfileRepository,
            employerProfileRepository,
            basicInfoRepository,
            employerBasicInfoRepository,
            contactRepository,
            mediaRepository,
            characteristicsRepository,
            passwordEncoder,
            jwtService,
            refreshTokenService,
            emailService,
            appProperties,
            messageSource,
            authContext,
            googleIdTokenVerifierService,
            userProvisioningService,
            legalService,
            talentWelcomeEmailService
        );
    }

    private RoleEntity role(String code) {
        RoleEntity role = new RoleEntity();
        role.setCode(code);
        return role;
    }

    private PlanEntity freePlan() {
        return PlanEntity.builder().id(UUID.randomUUID()).code("FREE").build();
    }

    private void stubHappyPathUpToUserCreation() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name())))
            .thenReturn(Optional.of(Set.of(role("TALENT"), role("EMPLOYER"))));
        when(planRepository.findByCode("FREE")).thenReturn(Optional.of(freePlan()));
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encoded-password");
        lenient().when(talentProfileRepository.findByUserId(any())).thenReturn(Optional.empty());
        lenient().when(employerProfileRepository.findByUserId(any())).thenReturn(Optional.empty());
        lenient().when(jwtService.generateTokenWithCustomExpirationTime(any(), any(), any(), any())).thenReturn("jwt-token");
        lenient().when(refreshTokenService.issue(any())).thenReturn("refresh-token");
    }

    @Test
    void register_duplicateEmail_throwsAndCreatesNothing() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.register(new RegisterRequest(EMAIL, PASSWORD)));

        assertEquals("auth.user_exists", exception.getMessage());
        verify(userRepository, never()).save(any());
        verify(talentProfileRepository, never()).save(any());
        verify(employerProfileRepository, never()).save(any());
    }

    @Test
    void register_missingRoles_throws() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name()))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.register(new RegisterRequest(EMAIL, PASSWORD)));

        assertEquals("auth.invalid_role", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_missingFreePlan_throwsAndCreatesNoUser() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(roleRepository.findAllByCodeIn(List.of(TALENT.name(), EMPLOYER.name())))
            .thenReturn(Optional.of(Set.of(role("TALENT"), role("EMPLOYER"))));
        when(planRepository.findByCode("FREE")).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> service.register(new RegisterRequest(EMAIL, PASSWORD)));

        assertEquals("auth.invalid_plan", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_createsBothTalentAndEmployerProfilesWithFreePlan() {
        PlanEntity freePlan = freePlan();
        stubHappyPathUpToUserCreation();
        when(planRepository.findByCode("FREE")).thenReturn(Optional.of(freePlan));

        service.register(new RegisterRequest(EMAIL, PASSWORD));

        ArgumentCaptor<TalentProfileEntity> talentCaptor = ArgumentCaptor.forClass(TalentProfileEntity.class);
        verify(talentProfileRepository).save(talentCaptor.capture());
        assertEquals(freePlan, talentCaptor.getValue().getPlan());

        ArgumentCaptor<EmployerProfileEntity> employerCaptor = ArgumentCaptor.forClass(EmployerProfileEntity.class);
        verify(employerProfileRepository).save(employerCaptor.capture());
        assertEquals(freePlan, employerCaptor.getValue().getPlan());
    }

    @Test
    void register_createsUserWithNotStartedOnboardingStatusesForBothModes() {
        stubHappyPathUpToUserCreation();

        service.register(new RegisterRequest(EMAIL, PASSWORD));

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals(com.padimasso.autocasting.application.auth.model.OnboardingStatus.NOT_STARTED, savedUser.getTalentOnboardingStatus());
        assertEquals(com.padimasso.autocasting.application.auth.model.OnboardingStatus.NOT_STARTED, savedUser.getEmployerOnboardingStatus());
    }

    @Test
    void register_createsEmptyEmployerBasicInfoShell() {
        stubHappyPathUpToUserCreation();

        service.register(new RegisterRequest(EMAIL, PASSWORD));

        ArgumentCaptor<EmployerBasicInfoEntity> captor = ArgumentCaptor.forClass(EmployerBasicInfoEntity.class);
        verify(employerBasicInfoRepository).save(captor.capture());
        assertEquals(1, captor.getAllValues().size());
    }

    @Test
    void register_createsTalentSupportingEntities() {
        stubHappyPathUpToUserCreation();

        service.register(new RegisterRequest(EMAIL, PASSWORD));

        verify(basicInfoRepository).save(any(BasicInfoEntity.class));
        verify(contactRepository).save(any(ContactEntity.class));
        verify(mediaRepository).save(any(MediaEntity.class));
        verify(characteristicsRepository).save(any(CharacteristicsEntity.class));
    }

    @Test
    void register_returnsJwtAndRefreshToken() {
        stubHappyPathUpToUserCreation();
        when(jwtService.generateTokenWithCustomExpirationTime(any(), any(), any(), any())).thenReturn("the-jwt");
        when(refreshTokenService.issue(any())).thenReturn("the-refresh-token");

        AuthResponse response = service.register(new RegisterRequest(EMAIL, PASSWORD));

        assertEquals("the-jwt", response.token());
        assertEquals("the-refresh-token", response.refreshToken());
    }

    @Test
    void register_passwordIsEncodedBeforeSaving() {
        stubHappyPathUpToUserCreation();
        when(passwordEncoder.encode(PASSWORD)).thenReturn("hashed-value");

        service.register(new RegisterRequest(EMAIL, PASSWORD));

        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("hashed-value", userCaptor.getValue().getPassword());
    }
}
