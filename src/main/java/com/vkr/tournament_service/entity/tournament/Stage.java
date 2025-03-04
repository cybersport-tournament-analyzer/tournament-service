package com.vkr.tournament_service.entity.tournament;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stage {
    SWISS("Swiss"),
    GROUPS("Groups"),
    SINGLE_ELIMINATION("Single Elimination"),
    DOUBLE_ELIMINATION("Double Elimination");

    private final String name;

    public static Stage fromName(String name) {
        for (Stage stage : Stage.values()) {
            if (stage.getName().equalsIgnoreCase(name)) {
                return stage;
            }
        }
        throw new IllegalArgumentException("No such stage: " + name);
    }
}