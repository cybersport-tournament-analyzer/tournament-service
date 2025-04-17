package com.vkr.tournament_service.service.prediction;

import com.vkr.tournament_service.entity.prediction.BracketPrediction;
import com.vkr.tournament_service.entity.prediction.PredictedMatch;
import com.vkr.tournament_service.repository.prediction.BracketPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BracketPredictionService {

    private final BracketPredictionRepository predictionRepository;

    public BracketPrediction submitPrediction(String userId, UUID stageId, UUID tournamentId, List<PredictedMatch> predictedMatches) {
        BracketPrediction prediction = predictionRepository
                .findByUserIdAndStageId(userId, stageId)
                .orElse(new BracketPrediction());

        prediction.setUserId(userId);
        prediction.setTournamentId(tournamentId);
        prediction.setStageId(stageId);
        prediction.setPredictedMatches(predictedMatches);
        prediction.setCreatedAt(LocalDateTime.now());

        return predictionRepository.save(prediction);
    }

    public Optional<BracketPrediction> getPrediction(String userId, UUID stageId) {
        return predictionRepository.findByUserIdAndStageId(userId, stageId);
    }

    public List<BracketPrediction> getAllPredictions(UUID stageId) {
        return predictionRepository.findAllByStageId(stageId);
    }
}

