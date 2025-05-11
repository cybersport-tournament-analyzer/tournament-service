package com.vkr.tournament_service.entity.prediction;

import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "bracket_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BracketPrediction {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "tournament_stage_id", nullable = false)
    private TournamentStage stage;

    @Lob
    @Column(name = "predicted_bracket", nullable = false)
    private String predictedBracketJson; // JSON представление сетки

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "stage_winner", nullable = false)
    private String stageWinner;

    @Column(name = "stage_third_place")
    private String stageThirdPlace;
}


