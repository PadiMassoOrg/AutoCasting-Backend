package com.padimasso.autocasting.application.auth.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
public class UserEntity extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAccountProvider userAccountProvider;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<RoleEntity> roles = Set.of();

    @Enumerated(EnumType.STRING)
    @Column(name = "active_mode")
    private UserMode activeMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "talent_onboarding_status", nullable = false)
    private OnboardingStatus talentOnboardingStatus = OnboardingStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "employer_onboarding_status", nullable = false)
    private OnboardingStatus employerOnboardingStatus = OnboardingStatus.NOT_STARTED;

    @Column(nullable = false)
    private boolean suspended = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        boolean hasAdmin = roles.stream().anyMatch(r -> r.getCode().equals("ADMIN"));
        if (hasAdmin) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        if (activeMode != null) {
            switch (activeMode) {
                case TALENT -> {
                    boolean hasTalent = roles.stream().anyMatch(r -> r.getCode().equals("TALENT"));
                    if (hasTalent) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_TALENT"));
                    }
                }
                case EMPLOYER -> {
                    boolean hasEmployer = roles.stream().anyMatch(r -> r.getCode().equals("EMPLOYER"));
                    if (hasEmployer) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYER"));
                    }
                }
            }
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !suspended;
    }

    @Override
    public boolean isEnabled() {
        return !suspended;
    }

}
