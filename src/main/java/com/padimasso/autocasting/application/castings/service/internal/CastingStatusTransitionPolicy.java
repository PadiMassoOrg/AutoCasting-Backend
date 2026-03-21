package com.padimasso.autocasting.application.castings.service.internal;

import com.padimasso.autocasting.application.castings.repository.projection.CastingCardStatusGateProjection;
import com.padimasso.autocasting.application.castings.repository.projection.CastingPublishGateProjection;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.*;

@Component
public class CastingStatusTransitionPolicy {

    /**
     * Fuente única de truth para “qué puede pasar desde el status actual”
     * (UX). El backend igual vuelve a validar en los endpoints.
     * <p>
     * Esta variante se usa en el listado (cards).
     */
    public List<String> allowedNextStatuses(CastingCardStatusGateProjection gate) {
        if (gate == null) return List.of();

        GateData g = GateData.from(gate);

        // deadline vencido: el casting no debería seguir "publicable/editable".
        // Si el job todavía no lo cerró, permitimos cerrar y/o archivar.
        if (isDeadlinePassed(g)) {
            String current = g.castingStatusCode();
            if (CASTING_STATUS_ARCHIVED.equals(current)) return List.of();
            if (CASTING_STATUS_CLOSED.equals(current)) return List.of(CASTING_STATUS_ARCHIVED);
            return List.of(CASTING_STATUS_CLOSED, CASTING_STATUS_ARCHIVED);
        }

        boolean publishAllowedNow = isPublishAllowedNow(g);

        return switch (g.castingStatusCode()) {

            case CASTING_STATUS_CLOSED -> List.of(CASTING_STATUS_ARCHIVED);

            case CASTING_STATUS_PUBLISHED -> List.of(CASTING_STATUS_PAUSED, CASTING_STATUS_CLOSED);

            case CASTING_STATUS_PAUSED -> {
                var out = new ArrayList<String>();
                if (publishAllowedNow) out.add(CASTING_STATUS_PUBLISHED);
                out.add(CASTING_STATUS_CLOSED);
                yield out;
            }

            default -> List.of();
        };
    }

    /**
     * Validaciónes
     */
    public void assertCanPublish(CastingPublishGateProjection gate) {
        if (gate == null) throw new IllegalStateException("castings.not_found");

        GateData g = GateData.from(gate);

        if (isPublishableSections(g)) {
            throw new IllegalStateException("castings.not_publishable");
        }

        if (g.applicationDeadline() == null) {
            throw new IllegalStateException("castings.deadline_required");
        }

        if (isDeadlinePassed(g)) {
            throw new IllegalStateException("castings.deadline_passed");
        }

        boolean canPublishFromStatus =
            CASTING_STATUS_DRAFT.equals(g.castingStatusCode()) || CASTING_STATUS_PAUSED.equals(g.castingStatusCode());

        if (!canPublishFromStatus) {
            throw new IllegalStateException("castings.invalid_status_transition");
        }
    }

    public void assertCanSetDraft(CastingPublishGateProjection gate) {
        if (gate == null) throw new IllegalStateException("castings.not_found");
        String s = gate.getCastingStatusCode();
        // Permitimos volver a DRAFT desde PUBLISHED o PAUSED (según tu policy)
        if (!(CASTING_STATUS_PUBLISHED.equals(s) || CASTING_STATUS_PAUSED.equals(s))) {
            throw new IllegalStateException("castings.invalid_status_transition");
        }
    }

    public void assertCanPause(CastingPublishGateProjection gate) {
        if (gate == null) throw new IllegalStateException("castings.not_found");
        // Pausar solo si está PUBLISHED
        if (!CASTING_STATUS_PUBLISHED.equals(gate.getCastingStatusCode())) {
            throw new IllegalStateException("castings.invalid_status_transition");
        }
    }

    public void assertCanClose(CastingPublishGateProjection gate) {
        if (gate == null) throw new IllegalStateException("castings.not_found");
        // Cerrar si está DRAFT/PUBLISHED/PAUSED (según tu allowedNextStatuses)
        String s = gate.getCastingStatusCode();
        if (CASTING_STATUS_ARCHIVED.equals(s) || CASTING_STATUS_CLOSED.equals(s)) {
            throw new IllegalStateException("castings.invalid_status_transition");
        }
        // Si deadline ya pasó, allowedNextStatuses igual habilita CLOSED/ARCHIVED.
        // Acá NO bloqueamos.
    }

    public void assertCanArchive(CastingPublishGateProjection gate) {
        if (gate == null) throw new IllegalStateException("castings.not_found");
        // Archivar si no está ya ARCHIVED
        if (CASTING_STATUS_ARCHIVED.equals(gate.getCastingStatusCode())) {
            throw new IllegalStateException("castings.invalid_status_transition");
        }
    }

    // =========================
    // Shared internal helpers
    // =========================
    private boolean isPublishableSections(GateData g) {
        return !CASTING_SECTION_STATUS_COMPLETED.equals(g.basicInfoStatusCode())
            || !CASTING_SECTION_STATUS_COMPLETED.equals(g.rolesStatusCode())
            || !CASTING_SECTION_STATUS_COMPLETED.equals(g.requirementsStatusCode())
            || !CASTING_SECTION_STATUS_COMPLETED.equals(g.remunerationStatusCode());
    }

    /**
     * Publish "ahora" = secciones completas + deadline presente + no vencido.
     * (Esto es lo que se usa para decidir si mostramos PUBLISHED como opción en DRAFT/PAUSED)
     */
    private boolean isPublishAllowedNow(GateData g) {
        if (isPublishableSections(g)) return false;
        if (g.applicationDeadline() == null) return false;
        return !isDeadlinePassed(g);
    }

    private boolean isDeadlinePassed(GateData g) {
        LocalDate d = g.applicationDeadline();
        return d != null && d.isBefore(LocalDate.now());
    }

    /**
     * Adapter interno para no duplicar lógica entre proyecciones distintas.
     */
    private record GateData(
        String castingStatusCode,
        String basicInfoStatusCode,
        String rolesStatusCode,
        String requirementsStatusCode,
        String remunerationStatusCode,
        LocalDate applicationDeadline
    ) {
        static GateData from(CastingCardStatusGateProjection p) {
            return new GateData(
                p.getCastingStatusCode(),
                p.getBasicInfoStatusCode(),
                p.getRolesStatusCode(),
                p.getRequirementsStatusCode(),
                p.getRemunerationStatusCode(),
                p.getApplicationDeadline()
            );
        }

        static GateData from(CastingPublishGateProjection p) {
            return new GateData(
                p.getCastingStatusCode(),
                p.getBasicInfoStatusCode(),
                p.getRolesStatusCode(),
                p.getRequirementsStatusCode(),
                p.getRemunerationStatusCode(),
                p.getApplicationDeadline()
            );
        }
    }
}
