package com.vkr.tournament_service.entity.tournamentStage;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.tournament.Tournament;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tournament_stages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentStage {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "stage_order", nullable = false)
    private int stageOrder;

    @Column(name = "stage_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Stage stageType;

    @Column(name = "current_round", nullable = false)
    private int currentRound;

    @Column(name = "total_rounds", nullable = false)
    private int totalRounds;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    private List<TournamentMatch> matches = new ArrayList<>();
}

