package com.vkr.tournament_service.entity.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentStatus {
    NOT_STARTED("NOT_STARTED"),
    REGISTRATION("REGISTRATION"),
    REGISTRATION_ENDED("REGISTRATION_ENDED"),
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED");

    private final String statusName;

    @Override
    public String toString() { return statusName; }

    public static TournamentStatus fromString(String statusName) {
        for(TournamentStatus status : TournamentStatus.values()) {
            if(status.statusName.equals(statusName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No such status: " + statusName);
    }
}
