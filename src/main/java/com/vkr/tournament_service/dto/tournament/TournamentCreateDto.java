package com.vkr.tournament_service.dto.tournament;

import com.vkr.tournament_service.dto.tournamentStage.TournamentStageCreateDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class TournamentCreateDto {
    @NotBlank
    private String tournamentName;

    @NotBlank
    private String creatorId;

    @Positive
    private Long teamsCount;

    @PositiveOrZero
    private int substitutionsNumber;

    @NotBlank
    private String tournamentMode;

    @Future
    private OffsetDateTime registrationStartTime;

    @Future
    private OffsetDateTime registrationEndTime;

    @Future
    private OffsetDateTime tournamentStartTime;

    @NotEmpty
    private List<TournamentStageCreateDto> stages;
}
