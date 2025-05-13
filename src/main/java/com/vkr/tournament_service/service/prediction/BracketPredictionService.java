package com.vkr.tournament_service.service.prediction;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.tournament_service.entity.prediction.BracketPrediction;
import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import com.vkr.tournament_service.exception.EntityNotFoundException;
import com.vkr.tournament_service.exception.ValidationException;
import com.vkr.tournament_service.repository.prediction.BracketPredictionRepository;
import com.vkr.tournament_service.repository.tournamentStage.TournamentStageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BracketPredictionService {

    private final BracketPredictionRepository predictionRepository;
    private final ObjectMapper objectMapper; // Для преобразования JSON
    private final TournamentStageRepository stageRepository;

    public BracketPrediction createPrediction(String userId, UUID stageId, List<List<List<Map<String, Object>>>> bracket, String stageWinner, String stageThirdPlace) {

        TournamentStage stage = stageRepository.findById(stageId).orElseThrow(()
                -> new EntityNotFoundException("Stage with id: " + stageId + " not found."));

        boolean anyFinished = stage.getMatches().stream().anyMatch(m -> m.getSchedule().getStatus() == ScheduleStatus.COMPLETED);
        if (anyFinished) {
            throw new ValidationException("Cannot submit prediction after matches in this stage have already started.");
        }

        Optional<BracketPrediction> existingPrediction = predictionRepository.findByUserIdAndStageId(userId, stageId);
        if (existingPrediction.isPresent()) {
            throw new ValidationException("User has already submitted a prediction for this stage.");
        }

        try {
            String json = objectMapper.writeValueAsString(bracket);
            BracketPrediction prediction = BracketPrediction.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .stage(stage)
                    .score(0)
                    .stageWinner(stageWinner)
                    .stageThirdPlace(stageThirdPlace)
                    .build();

            prediction.setPredictedBracketJson(json);
            return predictionRepository.save(prediction);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize prediction", e);
        }
    }

    @Transactional
    public List<BracketPrediction> getAllPredictionsByStage(UUID stageId) {
        return predictionRepository.findAllByStageIdOrderByScoreDesc(stageId);
    }

    public void deletePrediction(String userId, UUID predictionId) {
        Optional<BracketPrediction> predictionOpt = predictionRepository.findById(predictionId);

        if (predictionOpt.isEmpty()) {
            throw new EntityNotFoundException("Prediction not found.");
        }

        BracketPrediction prediction = predictionOpt.get();

        if (!prediction.getUserId().equals(userId)) {
            throw new ValidationException("You can only delete your own prediction.");
        }

        predictionRepository.delete(prediction);

    }

    public void updateScores(List<List<List<Map<String, Object>>>> actualBracket, TournamentStage stage) {
        List<BracketPrediction> predictions = predictionRepository.findAllByStageId(stage.getId());
        String actualWinner = getActualWinner(actualBracket, stage.getTotalRounds());
        String actualThirdPlace = getActualThirdPlace(actualBracket, stage.getTotalRounds());
        for (BracketPrediction prediction : predictions) {
            int newScore = calculatePredictionScore(prediction, actualBracket, actualWinner, actualThirdPlace);
            prediction.setScore(newScore);
        }

        predictionRepository.saveAll(predictions);
    }

    private String getActualWinner(List<List<List<Map<String, Object>>>> actualBracket, int finalRound) {
        var participant1 = actualBracket.get(finalRound - 1).get(0).get(0);
        var participant2 = actualBracket.get(finalRound - 1).get(0).get(1);

        if ((int) participant1.get("score") > (int) participant2.get("score")) {
            return (String) participant1.get("name");
        } else if ((int) participant1.get("score") < (int) participant2.get("score")) {
            return (String) participant2.get("name");
        } else {
            return null;
        }
    }

    private String getActualThirdPlace(List<List<List<Map<String, Object>>>> actualBracket, int finalRound) {
        if (actualBracket.get(finalRound - 1).size() == 1) {
            return null;
        } else {
            var participant1 = actualBracket.get(finalRound - 1).get(1).get(0);
            var participant2 = actualBracket.get(finalRound - 1).get(1).get(1);

            if ((int) participant1.get("score") > (int) participant2.get("score")) {
                return (String) participant1.get("name");
            } else if ((int) participant1.get("score") < (int) participant2.get("score")) {
                return (String) participant2.get("name");
            } else {
                return null;
            }
        }
    }

    public int calculatePredictionScore(
            BracketPrediction prediction,
            List<List<List<Map<String, Object>>>> actualBracket,
            String actualWinner,
            String actualThirdPlace
    ) {
        int score = 0;

        try {
            List<List<List<Map<String, Object>>>> predictedBracket =
                    objectMapper.readValue(prediction.getPredictedBracketJson(), new TypeReference<>() {
                    });

            for (int round = 1; round < Math.min(predictedBracket.size(), actualBracket.size()); round++) {
                List<List<Map<String, Object>>> predictedMatches = predictedBracket.get(round);
                List<List<Map<String, Object>>> actualMatches = actualBracket.get(round);

                for (int i = 0; i < Math.min(predictedMatches.size(), actualMatches.size()); i++) {
                    List<Map<String, Object>> predictedMatch = predictedMatches.get(i);
                    List<Map<String, Object>> actualMatch = actualMatches.get(i);

                    if (predictedMatch.size() != 2 || actualMatch.size() != 2) continue;

                    Set<String> predictedNames = new HashSet<>();
                    Set<String> actualNames = new HashSet<>();

                    for (Map<String, Object> team : predictedMatch) {
                        if (team.get("name") != null) {
                            predictedNames.add(team.get("name").toString());
                        }
                    }
                    for (Map<String, Object> team : actualMatch) {
                        if (team.get("name") != null) {
                            actualNames.add(team.get("name").toString());
                        }
                    }

                    // Начисляем 1 очко за каждого правильно угаданного участника матча
                    for (String name : predictedNames) {
                        if (actualNames.contains(name)) {
                            score += 1;
                        }
                    }
                }
            }

            // Победитель стадии
            if (prediction.getStageWinner() != null && actualWinner != null &&
                    prediction.getStageWinner().equals(actualWinner)) {
                score += 5;
            }

            // Победитель за 3-е место
            if (prediction.getStageThirdPlace() != null && actualThirdPlace != null &&
                    prediction.getStageThirdPlace().equals(actualThirdPlace)) {
                score += 3;
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while reading predictedBracketJson", e);
        }

        return score;
    }


}


