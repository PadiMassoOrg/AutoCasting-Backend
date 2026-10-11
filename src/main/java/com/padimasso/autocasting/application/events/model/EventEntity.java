package com.padimasso.autocasting.application.events.model;

import com.padimasso.autocasting.application.auth.model.UserEntity;
import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE events SET deleted = true WHERE id = ?")
public class EventEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, unique = true, length = 32)
    private String slug;

    @NotBlank(message = "events.name.required")
    @Column(nullable = false, length = 120)
    private String name;

    @NotNull(message = "events.entry_access.required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EntryAccess entryAccess;

    @Column(columnDefinition = "text")
    private String ticketUrl;

    @NotNull(message = "events.start_date.required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "events.end_date.required")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String datesInfo;

    @NotBlank(message = "events.location.required")
    @Column(nullable = false, length = 255)
    private String location;

    @NotBlank(message = "events.image_url.required")
    @Column(nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(columnDefinition = "text")
    private String description;

    // The ticket link is required for ONLINE access and must be absent otherwise (mirrors ck_events_ticket_url).
    @AssertTrue(message = "events.ticket_url.online_only_and_required")
    public boolean isTicketUrlConsistent() {
        if (entryAccess == null) return true;
        boolean hasTicketUrl = ticketUrl != null && !ticketUrl.isBlank();
        return entryAccess == EntryAccess.ONLINE ? hasTicketUrl : ticketUrl == null;
    }

    @AssertTrue(message = "events.date_range.invalid")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }

    @PrePersist
    @SuppressWarnings("unused")
    public void generateSlug() {
        if (this.slug == null || this.slug.isBlank()) {
            this.slug = "E-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
