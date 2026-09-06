CREATE SCHEMA IF NOT EXISTS platform;

-- ============================================================================
-- 1. Base Entities (No foreign key dependencies)
-- ============================================================================

-- platform.athletes
CREATE TABLE platform.athletes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(150) NOT NULL,
    nickname varchar(100) NULL,
    "number" int4 NULL,
    photo_url varchar(500) NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    cpf varchar(14) NULL,
    status varchar(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    birth_date date NULL,
    gender varchar(20) NULL,
    CONSTRAINT athletes_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_athletes_cpf ON platform.athletes USING btree (cpf) WHERE (cpf IS NOT NULL);

-- platform.venues
CREATE TABLE platform.venues (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(150) NOT NULL,
    address varchar(500) NULL,
    maps_url varchar(500) NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT venues_pkey PRIMARY KEY (id)
);

-- platform.organizations
CREATE TABLE platform.organizations (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    legal_name varchar(255) NOT NULL,
    trade_name varchar(255) NOT NULL,
    abbreviation varchar(255) NULL,
    organization_type varchar(255) NOT NULL,
    email varchar(255) NULL,
    phone varchar(255) NULL,
    website varchar(255) NULL,
    instagram varchar(255) NULL,
    country varchar(255) NOT NULL,
    state varchar(255) NULL,
    city varchar(255) NULL,
    logo_url varchar(255) NULL,
    primary_color varchar(255) NULL,
    secondary_color varchar(255) NULL,
    timezone varchar(255) NOT NULL,
    locale varchar(255) NOT NULL,
    status varchar(255) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    "document" varchar(255) NULL,
    document_type varchar(255) NULL,
    president_name varchar(255) NULL,
    president_cpf varchar(255) NULL,
    tertiary_color varchar(255) NULL,
    quaternary_color varchar(255) NULL,
    parent_id uuid NULL,
    CONSTRAINT ck_organizations_status CHECK (((status)::text = ANY (ARRAY[('ACTIVE'::character varying)::text, ('INACTIVE'::character varying)::text]))),
    CONSTRAINT ck_organizations_type CHECK (((organization_type)::text = ANY (ARRAY[('FEDERATION'::character varying)::text, ('LEAGUE'::character varying)::text, ('ASSOCIATION'::character varying)::text, ('UNIVERSITY'::character varying)::text, ('CLUB'::character varying)::text, ('OTHER'::character varying)::text]))),
    CONSTRAINT organizations_pkey PRIMARY KEY (id),
    CONSTRAINT uk_organizations_trade_name UNIQUE (trade_name),
    CONSTRAINT fk_organizations_parent FOREIGN KEY (parent_id) REFERENCES platform.organizations(id)
);

CREATE INDEX idx_organizations_parent_id ON platform.organizations USING btree (parent_id);
CREATE UNIQUE INDEX uk_organizations_document ON platform.organizations USING btree (document) WHERE (document IS NOT NULL);

-- platform.users
CREATE TABLE platform.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    password_hash varchar(255) NOT NULL,
    "role" varchar(255) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    status varchar(255) DEFAULT 'ACTIVE'::character varying NOT NULL,
    firebase_uid varchar(255) NULL,
    club_id uuid NULL,
    organization_id uuid NULL,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT users_firebase_uid_key UNIQUE (firebase_uid),
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- platform.event_publication (Spring Modulith)
CREATE TABLE platform.event_publication (
    id uuid NOT NULL,
    completion_attempts int4 NOT NULL,
    completion_date timestamptz(6) NULL,
    event_type varchar(255) NOT NULL,
    last_resubmission_date timestamptz(6) NULL,
    listener_id varchar(255) NOT NULL,
    publication_date timestamptz(6) NOT NULL,
    serialized_event varchar(255) NOT NULL,
    status varchar(255) NULL,
    CONSTRAINT event_publication_pkey PRIMARY KEY (id),
    CONSTRAINT event_publication_status_check CHECK (((status)::text = ANY ((ARRAY['PUBLISHED'::character varying, 'PROCESSING'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'RESUBMITTED'::character varying])::text[])))
);

-- ============================================================================
-- 2. Entities Dependent on Base Entities
-- ============================================================================

-- platform.athlete_positions
CREATE TABLE platform.athlete_positions (
    athlete_id uuid NOT NULL,
    "position" varchar(255) NOT NULL,
    CONSTRAINT fk_athlete_positions_athlete FOREIGN KEY (athlete_id) REFERENCES platform.athletes(id)
);

-- platform.password_reset_tokens
CREATE TABLE platform.password_reset_tokens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    user_id uuid NOT NULL,
    token_hash varchar(64) NOT NULL,
    expires_at timestamp NOT NULL,
    used_at timestamp NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES platform.users(id)
);

CREATE INDEX idx_password_reset_tokens_user ON platform.password_reset_tokens USING btree (user_id);

-- platform.user_skills
CREATE TABLE platform.user_skills (
    user_id uuid NOT NULL,
    skill varchar(255) NOT NULL,
    CONSTRAINT user_skills_pkey PRIMARY KEY (user_id, skill),
    CONSTRAINT user_skills_user_id_fkey FOREIGN KEY (user_id) REFERENCES platform.users(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_skills_user_id ON platform.user_skills USING btree (user_id);

-- platform.team
CREATE TABLE platform.team (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    "name" varchar(255) NOT NULL,
    short_name varchar(50) NULL,
    sport_name varchar(255) NULL,
    logo_url varchar(500) NULL,
    status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT team_pkey PRIMARY KEY (id),
    CONSTRAINT fk_team_organization FOREIGN KEY (organization_id) REFERENCES platform.organizations(id)
);

CREATE INDEX idx_team_organization ON platform.team USING btree (organization_id);

-- platform.competitions
CREATE TABLE platform.competitions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    organization_id uuid NOT NULL,
    "name" varchar(100) NOT NULL,
    description varchar(500) NULL,
    start_date date NULL,
    end_date date NULL,
    status varchar(255) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    gender varchar(20) NULL,
    age_group varchar(20) NULL,
    modality varchar(20) NOT NULL,
    grouping_type varchar(20) NULL,
    season varchar(50) DEFAULT '2026'::character varying NOT NULL,
    CONSTRAINT ck_competitions_dates CHECK (((end_date IS NULL) OR (start_date IS NULL) OR (end_date >= start_date))),
    CONSTRAINT ck_competitions_status CHECK (((status)::text = ANY (ARRAY[('DRAFT'::character varying)::text, ('PUBLISHED'::character varying)::text, ('FINISHED'::character varying)::text]))),
    CONSTRAINT competitions_pkey PRIMARY KEY (id),
    CONSTRAINT uk_competitions_organization_name UNIQUE (organization_id, name),
    CONSTRAINT fk_competitions_organization FOREIGN KEY (organization_id) REFERENCES platform.organizations(id)
);

-- ============================================================================
-- 3. Entities Dependent on Competitions / Teams
-- ============================================================================

-- platform.categories
CREATE TABLE platform.categories (
    id uuid NOT NULL,
    created_at timestamp(6) NULL,
    created_by uuid NULL,
    updated_at timestamp(6) NULL,
    updated_by uuid NULL,
    age_group varchar(20) NOT NULL,
    competition_id uuid NOT NULL,
    deleted_at timestamp(6) NULL,
    gender varchar(20) NOT NULL,
    modality_id uuid NOT NULL,
    "name" varchar(100) NULL,
    CONSTRAINT categories_age_group_check CHECK (((age_group)::text = ANY ((ARRAY['MASTER'::character varying, 'SUB20'::character varying, 'SUB11'::character varying, 'SUB14'::character varying, 'SUB13'::character varying, 'SUB15'::character varying, 'ADULT'::character varying, 'SUB17'::character varying, 'OPEN'::character varying])::text[]))),
    CONSTRAINT categories_gender_check CHECK (((gender)::text = ANY ((ARRAY['MIXED'::character varying, 'MALE'::character varying, 'FEMALE'::character varying])::text[]))),
    CONSTRAINT categories_pkey PRIMARY KEY (id)
);

-- platform.conferences
CREATE TABLE platform.conferences (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "name" varchar(100) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    competition_id uuid NOT NULL,
    CONSTRAINT conferences_pkey PRIMARY KEY (id),
    CONSTRAINT uk_conferences_competition_name UNIQUE (competition_id, name),
    CONSTRAINT fk_conferences_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id)
);

-- platform.divisions
CREATE TABLE platform.divisions (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    conference_id uuid NULL,
    "name" varchar(100) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    competition_id uuid NOT NULL,
    CONSTRAINT divisions_pkey PRIMARY KEY (id),
    CONSTRAINT fk_divisions_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id),
    CONSTRAINT fk_divisions_conference FOREIGN KEY (conference_id) REFERENCES platform.conferences(id)
);

CREATE UNIQUE INDEX uk_divisions_competition_conference_name ON platform.divisions USING btree (competition_id, COALESCE(conference_id, '00000000-0000-0000-0000-000000000000'::uuid), name);

-- platform.roster
CREATE TABLE platform.roster (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    team_id uuid NOT NULL,
    competition_id uuid NOT NULL,
    "name" varchar(255) NULL,
    season varchar(50) DEFAULT '2026'::character varying NOT NULL,
    status varchar(20) DEFAULT 'ACTIVE'::character varying NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT roster_pkey PRIMARY KEY (id),
    CONSTRAINT uk_roster_team_competition UNIQUE (team_id, competition_id),
    CONSTRAINT fk_roster_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id),
    CONSTRAINT fk_roster_team FOREIGN KEY (team_id) REFERENCES platform.team(id)
);

CREATE INDEX idx_roster_competition ON platform.roster USING btree (competition_id);
CREATE INDEX idx_roster_team ON platform.roster USING btree (team_id);

-- platform.rounds
CREATE TABLE platform.rounds (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    "number" int4 NOT NULL,
    "name" varchar(100) NULL,
    "type" varchar(255) DEFAULT 'REGULAR'::character varying NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    competition_id uuid NOT NULL,
    CONSTRAINT rounds_pkey PRIMARY KEY (id),
    CONSTRAINT uk_rounds_competition_number UNIQUE (competition_id, number),
    CONSTRAINT fk_rounds_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id)
);

-- platform.standings
CREATE TABLE platform.standings (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    team_id uuid NOT NULL,
    played int4 DEFAULT 0 NOT NULL,
    wins int4 DEFAULT 0 NOT NULL,
    draws int4 DEFAULT 0 NOT NULL,
    losses int4 DEFAULT 0 NOT NULL,
    goals_for int4 DEFAULT 0 NOT NULL,
    goals_against int4 DEFAULT 0 NOT NULL,
    points int4 DEFAULT 0 NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    competition_id uuid NOT NULL,
    CONSTRAINT standings_pkey PRIMARY KEY (id),
    CONSTRAINT uk_standings_competition_team UNIQUE (competition_id, team_id),
    CONSTRAINT fk_standings_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id)
);

-- platform.team_roster
CREATE TABLE platform.team_roster (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    roster_id uuid NOT NULL,
    athlete_id uuid NOT NULL,
    status varchar(255) NOT NULL,
    nickname varchar(100) NULL,
    "number" int4 NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT team_roster_pkey PRIMARY KEY (id),
    CONSTRAINT uk_team_roster_roster_athlete UNIQUE (roster_id, athlete_id),
    CONSTRAINT fk_team_roster_athlete FOREIGN KEY (athlete_id) REFERENCES platform.athletes(id),
    CONSTRAINT fk_team_roster_roster FOREIGN KEY (roster_id) REFERENCES platform.roster(id)
);

-- platform.competition_team
CREATE TABLE platform.competition_team (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    competition_id uuid NOT NULL,
    team_id uuid NOT NULL,
    division_id uuid NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT competition_team_pkey PRIMARY KEY (id),
    CONSTRAINT uk_competition_team UNIQUE (competition_id, team_id),
    CONSTRAINT fk_competition_team_competition FOREIGN KEY (competition_id) REFERENCES platform.competitions(id),
    CONSTRAINT fk_competition_team_division FOREIGN KEY (division_id) REFERENCES platform.divisions(id),
    CONSTRAINT fk_competition_team_team FOREIGN KEY (team_id) REFERENCES platform.team(id)
);

CREATE INDEX idx_competition_team_competition ON platform.competition_team USING btree (competition_id);
CREATE INDEX idx_competition_team_team ON platform.competition_team USING btree (team_id);

-- ============================================================================
-- 4. Entities Dependent on Rounds / Venues / Teams
-- ============================================================================

-- platform.games
CREATE TABLE platform.games (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    round_id uuid NOT NULL,
    home_team_id uuid NOT NULL,
    away_team_id uuid NOT NULL,
    venue_id uuid NULL,
    scheduled_at timestamp NULL,
    status varchar(255) DEFAULT 'SCHEDULED'::character varying NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    home_score int4 NULL,
    away_score int4 NULL,
    CONSTRAINT chk_games_teams_differ CHECK ((home_team_id <> away_team_id)),
    CONSTRAINT games_pkey PRIMARY KEY (id),
    CONSTRAINT uk_games_round_home_away UNIQUE (round_id, home_team_id, away_team_id),
    CONSTRAINT fk_games_round FOREIGN KEY (round_id) REFERENCES platform.rounds(id),
    CONSTRAINT fk_games_venue FOREIGN KEY (venue_id) REFERENCES platform.venues(id)
);

-- platform.plays
CREATE TABLE platform.plays (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    game_id uuid NOT NULL,
    team_id uuid NOT NULL,
    player_name varchar(100) NOT NULL,
    receiver_name varchar(100) NULL,
    play_type varchar(30) NOT NULL,
    description text NULL,
    yards int4 DEFAULT 0 NULL,
    quarter varchar(5) NULL,
    "time" varchar(10) NULL,
    is_first_down bool DEFAULT false NULL,
    is_touchdown bool DEFAULT false NULL,
    is_turnover bool DEFAULT false NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    CONSTRAINT plays_pkey PRIMARY KEY (id),
    CONSTRAINT fk_plays_game FOREIGN KEY (game_id) REFERENCES platform.games(id)
);

CREATE INDEX idx_plays_game_id ON platform.plays USING btree (game_id);
CREATE INDEX idx_plays_team_id ON platform.plays USING btree (team_id);

-- platform.score_events
CREATE TABLE platform.score_events (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    game_id uuid NOT NULL,
    team_id uuid NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT score_events_pkey PRIMARY KEY (id),
    CONSTRAINT fk_score_events_game FOREIGN KEY (game_id) REFERENCES platform.games(id)
);

-- platform.checkins
CREATE TABLE platform.checkins (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    game_id uuid NOT NULL,
    team_id uuid NOT NULL,
    athlete_id uuid NOT NULL,
    status varchar(255) NOT NULL,
    validated_by uuid NULL,
    validated_at timestamp NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp NULL,
    created_by uuid NULL,
    updated_by uuid NULL,
    match_number int4 NULL,
    CONSTRAINT checkins_pkey PRIMARY KEY (id),
    CONSTRAINT uk_checkins_game_athlete UNIQUE (game_id, athlete_id),
    CONSTRAINT fk_checkins_athlete FOREIGN KEY (athlete_id) REFERENCES platform.athletes(id),
    CONSTRAINT fk_checkins_game FOREIGN KEY (game_id) REFERENCES platform.games(id)
);
