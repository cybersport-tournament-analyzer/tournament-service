package com.vkr.tournament_service.entity.match;

import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournament.Tournament;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
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

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "format", nullable = false)
    private String matchFormat;

    @Column(name = "team1_score", nullable = false)
    private int team1Score;

    @Column(name = "team2_score", nullable = false)
    private int team2Score;

    @Column(name = "match_status", nullable = false)
    private String matchStatus;

    @ManyToOne
    @JoinColumn(name = "team1_name", nullable = false, referencedColumnName = "team_name")
    private TournamentTeam team1;

    @ManyToOne
    @JoinColumn(name = "team2_name", nullable = false, referencedColumnName = "team_name")
    private TournamentTeam team2;

    @Transient
    private List<Match> matches;

}
