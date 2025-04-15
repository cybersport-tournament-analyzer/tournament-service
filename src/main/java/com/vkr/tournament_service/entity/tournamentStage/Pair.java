package com.vkr.tournament_service.entity.tournamentStage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class Pair<F, S> {
    private final F first;
    private final S second;
}