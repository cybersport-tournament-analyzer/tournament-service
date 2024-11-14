package com.vkr.tournament_service.entity.tournament;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
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

    @JoinColumn(name = "creator_username", nullable = false)
    private String creatorUsername;

    @Column(name = "teams_count", nullable = false)
    private Long teamsCount;

    @Column(name = "winner_team_name", length = 255)
    private String winnerTeamName;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "tournament_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus tournamentStatus;

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
