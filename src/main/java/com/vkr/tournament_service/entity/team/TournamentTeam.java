package com.vkr.tournament_service.entity.team;

import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.tournament.Tournament;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "tournament_teams")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TournamentTeam {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "flag")
    private String flag;

    @Column(name = "creator_steam_id", nullable = false)
    private String creatorSteamId;

    @Column(name = "place")
    private String place;

    @Column(name = "average_rating")
    private int averageRating;

    @Column(name = "seed", nullable = false)
    private int seed;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players = new ArrayList<>();

    public List<Player> getMainRoster() {
        return players.stream().limit(tournament.getTeamPlayersNumber() - tournament.getSubstitutionsNumber()).collect(Collectors.toList());
    }

    public List<Player> getSubstitutes() {
        return players.stream().skip(tournament.getTeamPlayersNumber() - tournament.getSubstitutionsNumber()).collect(Collectors.toList());
    }

}
