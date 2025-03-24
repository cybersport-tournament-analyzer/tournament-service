package com.vkr.tournament_service.entity.tournament;


import com.vkr.tournament_service.entity.match.TournamentMatch;
import com.vkr.tournament_service.entity.player.Player;
import com.vkr.tournament_service.entity.team.TournamentTeam;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "tournament_name", length = 255, nullable = false)
    private String tournamentName;

    @Column(name = "creator_id", length = 255, nullable = false)
    private String creatorId;

    @Column(name = "teams_count", nullable = false)
    private Long teamsCount;

    @Column(name = "tournament_mode", nullable = false)
    private String tournamentMode;

    @Column(name = "winner_team_name")
    private String winnerTeamName;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "registration_start_time", nullable = false)
    private OffsetDateTime registrationStartTime;

    @Column(name = "registration_end_time", nullable = false)
    private OffsetDateTime registrationEndTime;

    @Column(name = "tournament_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus tournamentStatus;

    @ElementCollection
    @CollectionTable(name = "tournament_stages", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "stage_name")
    @Enumerated(EnumType.STRING)
    private List<Stage> stages = new ArrayList<>();

    @Column(name = "current_stage_name", nullable = false)
    private String currentStageName;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TournamentMatch> matches = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TournamentTeam> teams = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tournament_players",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    @MapKeyColumn(name = "team_name")
    private Map<String, Player> players;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Tournament tournament = (Tournament) o;
        return getId() != null && Objects.equals(getId(), tournament.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
