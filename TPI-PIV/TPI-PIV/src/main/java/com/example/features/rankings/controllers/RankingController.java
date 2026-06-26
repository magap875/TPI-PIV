package com.example.features.rankings.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.features.rankings.dtos.RankingResponseDTO;
import com.example.features.rankings.services.interfaces.IRankingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    private final IRankingService rankingService;

    @GetMapping("/global")
    public List<RankingResponseDTO> obtenerRankingGlobal() {
        return rankingService.obtenerRankingGlobal();
    }
}