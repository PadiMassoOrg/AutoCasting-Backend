package com.padimasso.autocasting.application.legal.model;

import com.padimasso.autocasting.application.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "legal_documents",
    uniqueConstraints = @UniqueConstraint(columnNames = {"type","locale","version"})
)
@Getter @Setter @NoArgsConstructor
public class LegalDocument extends AuditableEntity {

    @Id @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LegalDocumentType type;

    @Column(nullable = false, length = 8)
    private String locale;

    @Column(nullable = false, length = 32)
    private String version;

    @Column(name = "title", nullable = false, columnDefinition = "text")
    private String title;

    @Column(name = "slug", nullable = false, columnDefinition = "text")
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LegalDocumentStatus status = LegalDocumentStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentFormat format = ContentFormat.HTML;

    // ⬇️ Quitar @Lob y forzar text
    @Column(name = "body_template", nullable = false, columnDefinition = "text")
    private String bodyTemplate;
    // Alternativa equivalente:
    // @JdbcTypeCode(SqlTypes.LONGVARCHAR)  // si prefieres sin columnDefinition

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "effective_at", nullable = false)
    private OffsetDateTime effectiveAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;
}
