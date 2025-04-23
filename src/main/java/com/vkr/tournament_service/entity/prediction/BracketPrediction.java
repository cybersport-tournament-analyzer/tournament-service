package com.vkr.tournament_service.entity.prediction;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bracket_predictions")
@Getter
@Setter
public class BracketPrediction {

    @Id
    @UuidGenerator
    private UUID id;

    private UUID tournamentId;

    private String userId;

    private UUID stageId; // ID стадии сетки, если сеток несколько

    @ElementCollection
    @CollectionTable(name = "bracket_prediction_matches", joinColumns = @JoinColumn(name = "prediction_id"))
    private List<PredictedMatch> predictedMatches = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}

