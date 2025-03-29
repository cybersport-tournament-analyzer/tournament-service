package com.vkr.tournament_service.entity.tournamentStage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Stage {
    SWISS("Swiss"),
    GROUPS("Groups"),
    SINGLE_ELIMINATION("Single Elimination"),
    DOUBLE_ELIMINATION("Double Elimination");

    private final String name;

    public static Stage fromName(String name) {
        return Arrays.stream(values())
                .filter(stage -> stage.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown stage type: " + name));
    }
}