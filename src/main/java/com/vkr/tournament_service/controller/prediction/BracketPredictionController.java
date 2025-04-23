package com.vkr.tournament_service.controller.prediction;

import com.vkr.tournament_service.dto.prediction.BracketPredictionRequest;
import com.vkr.tournament_service.entity.prediction.BracketPrediction;
import com.vkr.tournament_service.entity.prediction.PredictedMatch;
import com.vkr.tournament_service.service.prediction.BracketPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class BracketPredictionController {

    private final BracketPredictionService predictionService;

    @PostMapping
    public ResponseEntity<BracketPrediction> submitPrediction(@RequestBody BracketPredictionRequest request) {

        List<PredictedMatch> matches = request.getPredictedMatches().stream()
                .map(dto -> {
                    PredictedMatch match = new PredictedMatch();
                    match.setMatchId(dto.getMatchId());
                    match.setPredictedWinnerName(dto.getPredictedWinnerName());
                    return match;
                })
                .toList();

        BracketPrediction prediction = predictionService.submitPrediction(
                request.getUserId(),
                request.getStageId(),
                request.getTournamentId(),
                matches
        );
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/{stageId}/user/{userId}")
    public ResponseEntity<BracketPrediction> getUserPrediction(@PathVariable UUID stageId, @PathVariable String userId) {
        return predictionService.getPrediction(userId, stageId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<List<BracketPrediction>> getAllPredictions(@PathVariable UUID stageId) {
        return ResponseEntity.ok(predictionService.getAllPredictions(stageId));
    }
}

