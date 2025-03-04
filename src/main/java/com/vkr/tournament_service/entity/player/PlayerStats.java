package com.vkr.tournament_service.entity.player;

import lombok.Data;

@Data
public class PlayerStats {

    private int _1x1MatchesPlayed;
    private int _2x2MatchesPlayed;
    private int _5x5MatchesPlayed;

    private int _1x1TournamentsPlayed;
    private int _2x2TournamentsPlayed;
    private int _5x5TournamentsPlayed;

    private int _1x1MatchesWins;
    private int _2x2MatchesWins;
    private int _5x5MatchesWins;

    private int _1x1MatchesLosses;
    private int _2x2MatchesLosses;
    private int _5x5MatchesLosses;

    private int _1x1TournamentsWins;
    private int _2x2TournamentsWins;
    private int _5x5TournamentsWins;
}
