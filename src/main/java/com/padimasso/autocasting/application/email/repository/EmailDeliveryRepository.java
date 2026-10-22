package com.padimasso.autocasting.application.email.repository;

import com.padimasso.autocasting.application.email.model.EmailDeliveryEntity;
import com.padimasso.autocasting.application.email.repository.projection.EmailLastSentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EmailDeliveryRepository extends JpaRepository<EmailDeliveryEntity, UUID> {

    @Query("""
        select d.userId as userId, d.emailType as emailType, max(d.sentAt) as lastSentAt
        from EmailDeliveryEntity d
        where d.deleted = false
          and d.userId in :userIds
        group by d.userId, d.emailType
        """)
    List<EmailLastSentProjection> findLastSentAtPerTypeByUserIds(@Param("userIds") Collection<UUID> userIds);
}
