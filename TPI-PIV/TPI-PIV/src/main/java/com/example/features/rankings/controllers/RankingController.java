package com.example.features.rankings.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.features.rankings.dtos.RankingResponseDTO;
import com.example.features.rankings.services.interfaces.IRankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Rankings", description = "Ranking global de usuarios del torneo")
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    private final IRankingService rankingService;

    @Operation(summary = "Obtener el ranking global de usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking obtenido correctamente (ordenado por puntos, resultados exactos, y antigüedad de pronósticos como criterios de desempate)")
    })
    @GetMapping("/global")
    public List<RankingResponseDTO> obtenerRankingGlobal() {
        return rankingService.obtenerRankingGlobal();
    }
}