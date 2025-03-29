package com.vkr.tournament_service.entity.match;

import com.vkr.tournament_service.entity.schedule.TournamentSchedule;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tournament_matches")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentMatch {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private TournamentSchedule schedule;

    @ManyToOne
    @JoinColumn(name = "stage_id")
    private TournamentStage stage;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "format", nullable = false)
    private String matchFormat;

    @Column(name = "team1_score", nullable = false)
    private int team1Score;

    @Column(name = "team2_score", nullable = false)
    private int team2Score;

    @Column(name = "winner_team_name")
    private String winnerTeamName;

    @Column(name = "match_status", nullable = false)
    private String matchStatus;

    @ManyToOne
    @JoinColumn(name = "team1_name", nullable = false, referencedColumnName = "team_name")
    private TournamentTeam team1;

    @ManyToOne
    @JoinColumn(name = "team2_name", nullable = false, referencedColumnName = "team_name")
    private TournamentTeam team2;

    @Column(name = "round", nullable = false)
    private int round;

    @Transient
    private List<Match> matches = new ArrayList<>();

}
