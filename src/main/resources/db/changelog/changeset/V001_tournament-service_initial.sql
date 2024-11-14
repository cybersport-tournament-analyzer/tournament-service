create table tournaments
(
    id                uuid         not null,
    tournament_name   varchar(255) not null,
    creator_username  varchar(255) not null,
    teams_count       int          not null,
    winner_team_name  varchar(255),
    created_at        timestamp    not null,
    tournament_status varchar(255) not null,
    constraint pk_tournaments primary key (id)
);

alter table tournaments
    add constraint fk_tournament_on_user_username foreign key (creator_username) references users (username);
