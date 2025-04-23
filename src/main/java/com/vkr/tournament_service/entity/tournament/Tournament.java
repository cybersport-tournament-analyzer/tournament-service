package com.vkr.tournament_service.entity.tournament;


import com.vkr.tournament_service.entity.team.TournamentTeam;
import com.vkr.tournament_service.entity.tournamentStage.TournamentStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Column(name = "substitutions_number", nullable = false)
    private int substitutionsNumber;

    @Column(name = "tournament_mode", nullable = false)
    private String tournamentMode;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "registration_start_time", nullable = false)
    private OffsetDateTime registrationStartTime;

    @Column(name = "registration_end_time", nullable = false)
    private OffsetDateTime registrationEndTime;

    @Column(name = "tournament_start_time", nullable = false)
    private OffsetDateTime tournamentStartTime;

    @Column(name = "tournament_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus tournamentStatus;

    @Column(name = "current_stage_order", nullable = false)
    private int currentStageOrder;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentStage> stages = new ArrayList<>();

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentTeam> teams = new ArrayList<>();


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

    public int getTeamPlayersNumber() {
        return switch (tournamentMode) {
            case "1vs1" -> 1 + substitutionsNumber;
            case "2vs2" -> 2 + substitutionsNumber;
            case "5vs5" -> 5 + substitutionsNumber;
            default -> throw new IllegalArgumentException("Unknown tournament mode: " + tournamentMode);
        };
    }

}
