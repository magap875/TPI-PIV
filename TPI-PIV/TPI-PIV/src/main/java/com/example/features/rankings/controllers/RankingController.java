// package com.example.features.rankings.controllers;

// import lombok.AllArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import com.example.features.rankings.dtos.RankingResponseDTO;
// import com.example.features.rankings.services.interfaces.IRankingService;
// import java.util.List;

// @RestController
// @RequestMapping("/api/ranking")
// @AllArgsConstructor
// public class RankingController {

//     private final IRankingService rankingService;

//     @GetMapping("/global")
//     public ResponseEntity<List<RankingResponseDTO>> rankingGlobal() {
//         return ResponseEntity.ok(rankingService.rankingGlobal());
//     }

//     @GetMapping("/grupos/{grupoId}")
//     public ResponseEntity<List<RankingResponseDTO>> rankingGrupo(
//             @PathVariable Long grupoId
//     ) {
//         return ResponseEntity.ok(rankingService.rankingGrupo(grupoId));
//     }
// }
