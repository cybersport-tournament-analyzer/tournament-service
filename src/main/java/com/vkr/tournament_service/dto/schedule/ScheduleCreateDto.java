package com.vkr.tournament_service.dto.schedule;

import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class ScheduleCreateDto {
    @NotNull
    private UUID matchId;
    @NotNull
    private OffsetDateTime scheduledStartTime;
    private OffsetDateTime scheduledEndTime;
    @NotNull
    private ScheduleStatus status;
}

