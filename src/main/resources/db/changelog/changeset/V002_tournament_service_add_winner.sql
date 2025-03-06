alter table tournament_matches
    add column winner_team_name varchar(255);

alter table tournaments
    rename column tournament_format to tournament_mode;