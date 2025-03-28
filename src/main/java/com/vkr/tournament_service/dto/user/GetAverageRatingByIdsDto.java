package com.vkr.tournament_service.dto.user;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class GetAverageRatingByIdsDto {

    private List<String> ids;
    private int playersNumber;
}

