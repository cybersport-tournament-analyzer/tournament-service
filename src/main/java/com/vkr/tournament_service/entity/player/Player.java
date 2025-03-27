package com.vkr.tournament_service.entity.player;

import com.vkr.tournament_service.entity.team.TournamentTeam;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "players")
public class Player {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "player_steam_id", nullable = false)
    private String playerSteamId;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "player_in_game_roles",
            joinColumns = @JoinColumn(name = "player_id")
    )
    @Column(name = "role")
    private List<InGameRole> inGameRoles;

    @ManyToMany(mappedBy = "players")
    private List<TournamentTeam> teams;
}
