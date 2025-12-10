package com.padimasso.autocasting.application.castings.controller;

import com.padimasso.autocasting.application.castings.dto.response.CastingCardResponse;
import com.padimasso.autocasting.application.castings.dto.response.CastingResponse;
import com.padimasso.autocasting.application.castings.service.CastingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.padimasso.autocasting.config.AppConstants.CASTINGS_URL;
import static com.padimasso.autocasting.config.AppConstants.PUBLIC_CASTINGS_URL;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Castings", description = "Operaciones relacionadas a los Castings de un empleador")
@SuppressWarnings("unused")
public class CastingController {

    private final CastingService castingService;

    @Operation(
        summary = "Creacion de un nuevo Casting",
        description = "Permite a un Employer crear un nuevo Casting (vacío)."
    )
    @PostMapping(CASTINGS_URL)
    public ResponseEntity<String> createEmptyCasting() {
        return ResponseEntity.ok(castingService.createEmptyCasting());
    }

    @Operation(
        summary = "Listado de mis Castings",
        description = "Permite a un Employer ver sus Castings. CARDS"
    )
    @GetMapping(CASTINGS_URL)
    public ResponseEntity<List<CastingCardResponse>> getMyCastingsCards() {
        return ResponseEntity.ok(castingService.getMyCastings());
    }

    // Public
    @Operation(
        summary = "Listado de Castings",
        description = "Permite a un usuario ver Castings. CARDS"
    )
    @GetMapping(PUBLIC_CASTINGS_URL)
    public ResponseEntity<List<CastingCardResponse>> getCastingCards() {
        return ResponseEntity.ok(castingService.getCastingsCards());
    }

    @Operation(summary = "Ver detalles del Casting", description = "Obtiene información pública del Casting por slug")
    @GetMapping(PUBLIC_CASTINGS_URL + "/{slug}")
    public ResponseEntity<CastingResponse> getCastingDetails(@PathVariable String slug) {
        return ResponseEntity.ok(castingService.getDetailsBySlug(slug));
    }
    
}
