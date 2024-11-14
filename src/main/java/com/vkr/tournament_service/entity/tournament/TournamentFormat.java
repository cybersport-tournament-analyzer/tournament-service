package com.vkr.tournament_service.entity.tournament;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum TournamentFormat {
    SINGLE_ELIMINATION("Single elimination"),
    DOUBLE_ELIMINATION("Double elimination"),
    ROUND_ROBIN("Round robin"),
    SWISS("Swiss");

    private final String formatName;

    @Override
    public String toString() {
        return formatName;
    }

    public static TournamentFormat fromString(String formatName) {
        for(TournamentFormat format : TournamentFormat.values()) {
            if(format.formatName.equals(formatName)) {
                return format;
            }
        }
        throw new IllegalArgumentException("No such format: " + formatName);
    }
}
