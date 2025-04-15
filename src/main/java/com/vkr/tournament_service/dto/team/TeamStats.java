package com.vkr.tournament_service.dto.team;

import lombok.Getter;

public class TeamStats {
    @Getter
    private int wins = 0;
    @Getter
    private int losses = 0;
    @Getter
    private int roundsWon = 0;
    @Getter
    private int roundsLost = 0;
    private final String groupLetter;

    public TeamStats(String groupLetter) {
        this.groupLetter = groupLetter;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLosses() {
        losses++;
    }

    public void addRounds(int won, int lost) {
        this.roundsWon += won;
        this.roundsLost += lost;
    }

    public int getPoints() {
        return wins * 2;
    }

    public int getRoundDifference() {
        return roundsWon - roundsLost;
    }
}


