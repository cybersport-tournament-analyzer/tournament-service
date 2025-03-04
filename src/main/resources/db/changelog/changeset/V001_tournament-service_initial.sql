CREATE TABLE players (
                         id UUID PRIMARY KEY,
                         player_username VARCHAR(255) NOT NULL,
                         player_steam_id VARCHAR(255) NOT NULL,
                         rating INT
);

CREATE TABLE tournaments (
                             id UUID PRIMARY KEY,
                             tournament_name VARCHAR(255) NOT NULL,
                             creator_username VARCHAR(255) NOT NULL,
                             teams_count BIGINT NOT NULL,
                             tournament_format VARCHAR(255) NOT NULL,
                             winner_team_name VARCHAR(255),
                             created_at TIMESTAMP NOT NULL,
                             tournament_status VARCHAR(50) NOT NULL,
                             current_stage_name VARCHAR(255) NOT NULL
);

CREATE TABLE tournament_teams (
                                  id UUID PRIMARY KEY,
                                  tournament_id UUID NOT NULL,
                                  team_name VARCHAR(255) NOT NULL UNIQUE,
                                  flag VARCHAR(255),
                                  creator_username VARCHAR(255) NOT NULL,
                                  place VARCHAR(255),
                                  FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);

CREATE TABLE tournament_matches (
                                    id UUID PRIMARY KEY,
                                    start_time TIMESTAMP NOT NULL,
                                    tournament_id UUID NOT NULL,
                                    format VARCHAR(255) NOT NULL,
                                    team1_score INT NOT NULL,
                                    team2_score INT NOT NULL,
                                    match_status VARCHAR(50) NOT NULL,
                                    team1_name VARCHAR(255) NOT NULL,
                                    team2_name VARCHAR(255) NOT NULL,
                                    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
                                    FOREIGN KEY (team1_name) REFERENCES tournament_teams(team_name),
                                    FOREIGN KEY (team2_name) REFERENCES tournament_teams(team_name)
);

CREATE TABLE tournament_stages (
                                   tournament_id UUID NOT NULL,
                                   stage_name VARCHAR(255) NOT NULL,
                                   FOREIGN KEY (tournament_id) REFERENCES tournaments(id)
);

CREATE TABLE team_players (
                              team_id UUID NOT NULL,
                              player_id UUID NOT NULL,
                              PRIMARY KEY (team_id, player_id),
                              FOREIGN KEY (team_id) REFERENCES tournament_teams(id),
                              FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE TABLE tournament_players (
                                    tournament_id UUID NOT NULL,
                                    player_id UUID NOT NULL,
                                    team_name VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (tournament_id, player_id),
                                    FOREIGN KEY (tournament_id) REFERENCES tournaments(id),
                                    FOREIGN KEY (player_id) REFERENCES players(id)
);