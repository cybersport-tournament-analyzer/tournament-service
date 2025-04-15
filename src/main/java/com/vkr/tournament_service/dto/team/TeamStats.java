package com.vkr.tournament_service.dto.team;

public class TeamStats {
    private final String groupLetter;
    private int wins;
    private int losses;

    public TeamStats(String groupLetter) {
        this.groupLetter = groupLetter;
    }

    public void incrementWins() {
        wins++;
    }

    public void incrementLosses() {
        losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public String getGroupLetter() {
        return groupLetter;
    }
}

