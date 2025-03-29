package com.vkr.tournament_service.entity.schedule;

import com.vkr.tournament_service.entity.match.TournamentMatch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tournament_schedules")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TournamentSchedule {

    @Id
    @UuidGenerator
    private UUID id;

    @OneToOne
    @JoinColumn(name = "match_id", nullable = false)
    private TournamentMatch match;

    @Column(name = "scheduled_start_time", nullable = false)
    private OffsetDateTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private OffsetDateTime scheduledEndTime;

    @Column(name = "actual_start_time")
    private OffsetDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private OffsetDateTime actualEndTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
}
