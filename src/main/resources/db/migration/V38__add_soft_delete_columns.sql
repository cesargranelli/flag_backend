-- Soft Delete: todas as entidades que herdam de BaseEntity recebem deleted_at
-- para suporte à sincronização com Firestore (ADR-006).

-- Entidades que herdam de BaseEntity (tabelas a modificar):
-- organizations, competitions, venues, team, rounds, games,
-- athletes, roster, team_roster, users, conferences,
-- divisions, standings, plays, checkins, competition_team

-- 1. organizations
ALTER TABLE platform.organizations
    ADD COLUMN deleted_at TIMESTAMP;

-- 2. competitions
ALTER TABLE platform.competitions
    ADD COLUMN deleted_at TIMESTAMP;

-- 3. venues
ALTER TABLE platform.venues
    ADD COLUMN deleted_at TIMESTAMP;

-- 4. team
ALTER TABLE platform.team
    ADD COLUMN deleted_at TIMESTAMP;

-- 5. rounds
ALTER TABLE platform.rounds
    ADD COLUMN deleted_at TIMESTAMP;

-- 6. games
ALTER TABLE platform.games
    ADD COLUMN deleted_at TIMESTAMP;

-- 7. athletes
ALTER TABLE platform.athletes
    ADD COLUMN deleted_at TIMESTAMP;

-- 8. roster
ALTER TABLE platform.roster
    ADD COLUMN deleted_at TIMESTAMP;

-- 9. team_roster
ALTER TABLE platform.team_roster
    ADD COLUMN deleted_at TIMESTAMP;

-- 10. users
ALTER TABLE platform.users
    ADD COLUMN deleted_at TIMESTAMP;

-- 11. conferences
ALTER TABLE platform.conferences
    ADD COLUMN deleted_at TIMESTAMP;

-- 12. divisions
ALTER TABLE platform.divisions
    ADD COLUMN deleted_at TIMESTAMP;

-- 13. standings
ALTER TABLE platform.standings
    ADD COLUMN deleted_at TIMESTAMP;

-- 14. plays
ALTER TABLE platform.plays
    ADD COLUMN deleted_at TIMESTAMP;

-- 15. checkins
ALTER TABLE platform.checkins
    ADD COLUMN deleted_at TIMESTAMP;

-- 16. competition_team
ALTER TABLE platform.competition_team
    ADD COLUMN deleted_at TIMESTAMP;

-- Índices para acelerar consultas de itens ativos (filtro deleted_at IS NULL)
CREATE INDEX idx_organizations_deleted_at ON platform.organizations (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_competitions_deleted_at ON platform.competitions (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_venues_deleted_at ON platform.venues (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_team_deleted_at ON platform.team (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_rounds_deleted_at ON platform.rounds (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_games_deleted_at ON platform.games (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_athletes_deleted_at ON platform.athletes (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_roster_deleted_at ON platform.roster (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_team_roster_deleted_at ON platform.team_roster (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_deleted_at ON platform.users (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_conferences_deleted_at ON platform.conferences (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_divisions_deleted_at ON platform.divisions (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_standings_deleted_at ON platform.standings (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_plays_deleted_at ON platform.plays (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_checkins_deleted_at ON platform.checkins (deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_competition_team_deleted_at ON platform.competition_team (deleted_at) WHERE deleted_at IS NULL;
