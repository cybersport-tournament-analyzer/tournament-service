package com.vkr.tournament_service.entity.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentStage {
    GROUPS("Groups"),
    PLAYOFF("Play-off"),
    LEAGUE("League");

    private final String stageName;

    @Override
    public String toString() { return stageName; }

    public static TournamentStage fromString(String stageName) {
        for(TournamentStage stage : TournamentStage.values()) {
            if(stage.stageName.equals(stageName)) {
                return stage;
            }
        }
        throw new IllegalArgumentException("No such stage: " + stageName);
    }
}
