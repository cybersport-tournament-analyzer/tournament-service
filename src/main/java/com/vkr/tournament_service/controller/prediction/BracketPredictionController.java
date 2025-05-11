package com.vkr.tournament_service.controller.prediction;

import com.vkr.tournament_service.dto.prediction.BracketPredictionDto;
import com.vkr.tournament_service.dto.prediction.BracketPredictionRequestDto;
import com.vkr.tournament_service.mapper.prediction.BracketPredictionMapper;
import com.vkr.tournament_service.service.prediction.BracketPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
public class BracketPredictionController {

    private final BracketPredictionService predictionService;
    private final BracketPredictionMapper bracketPredictionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create prediction")
    public BracketPredictionDto createPrediction(@RequestBody BracketPredictionRequestDto predictionRequestDto) {
        return bracketPredictionMapper
                .toDto(predictionService.createPrediction(predictionRequestDto.getUserId(),
                        UUID.fromString(predictionRequestDto.getStageId()), predictionRequestDto.getBracket(),
                        predictionRequestDto.getStageWinner(), predictionRequestDto.getStageThirdPlace()));
    }

    @GetMapping("/{stageId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get predictions by stageId")
    public List<BracketPredictionDto> getAllPredictionsByStage(@PathVariable String stageId) {
        return predictionService.getAllPredictionsByStage(UUID.fromString(stageId)).stream()
                .map(bracketPredictionMapper::toDto).collect(Collectors.toList());
    }

    @DeleteMapping("/{predictionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete prediction")
    public void deletePrediction(@PathVariable String predictionId, @RequestParam String userId) {
        predictionService.deletePrediction(userId, UUID.fromString(predictionId));
    }
}

