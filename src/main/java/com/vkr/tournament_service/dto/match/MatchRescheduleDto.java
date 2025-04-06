package com.vkr.tournament_service.dto.match;

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
public class MatchRescheduleDto {
    private OffsetDateTime newStartTime;
    private String userId;
}
