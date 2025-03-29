package com.vkr.tournament_service.dto.schedule;

import com.vkr.tournament_service.entity.schedule.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class ScheduleUpdateDto {
    private OffsetDateTime scheduledStartTime;
    private OffsetDateTime scheduledEndTime;
    private OffsetDateTime actualStartTime;
    private OffsetDateTime actualEndTime;
    private ScheduleStatus status;
}
