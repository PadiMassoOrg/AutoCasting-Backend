package com.padimasso.autocasting.application.auth.repository;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.config.jpa.SoftDeleteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends SoftDeleteRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    boolean existsByEmailUnsubscribeToken(String emailUnsubscribeToken);

    @Query("select u.emailUnsubscribeToken from UserEntity u where u.id = :userId")
    Optional<String> findEmailUnsubscribeTokenById(@Param("userId") UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update UserEntity u
           set u.emailUnsubscribeToken = :token
         where u.id = :userId
           and u.emailUnsubscribeToken is null
        """)
    int assignEmailUnsubscribeTokenIfMissing(@Param("userId") UUID userId, @Param("token") String token);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update UserEntity u
           set u.marketingEmailsUnsubscribedAt = :unsubscribedAt
         where u.emailUnsubscribeToken = :token
           and u.marketingEmailsUnsubscribedAt is null
        """)
    int markMarketingEmailsUnsubscribed(@Param("token") String token, @Param("unsubscribedAt") LocalDateTime unsubscribedAt);
}
