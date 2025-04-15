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

    @Column(name = "total_rounds", nullable = false)
    private int totalRounds;

    @Column(name = "final_match_format")
    private String finalMatchFormat;

    @Column(name = "match_format", nullable = false)
    private String matchFormat;

    @Column(name = "match_for_the_third_place")
    private boolean matchForTheThirdPlace;

    @Column(name = "number_of_groups")
    private Integer numberOfGroups;

    @Column(name = "teams_to_advance")
    private Integer teamsToAdvance;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentMatch> matches = new ArrayList<>();
}

