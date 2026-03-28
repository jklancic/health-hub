-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users
CREATE TABLE users (
    id                      UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    username                VARCHAR(50) NOT NULL UNIQUE,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    password                VARCHAR(255) NOT NULL,
    first_name              VARCHAR(255),
    last_name               VARCHAR(255),
    account_non_expired     BOOLEAN     NOT NULL DEFAULT TRUE,
    account_non_locked      BOOLEAN     NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN     NOT NULL DEFAULT TRUE,
    enabled                 BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP,
    updated_at              TIMESTAMP
);

-- User roles (ElementCollection)
CREATE TABLE user_roles (
    user_id UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- User profiles
CREATE TABLE user_profiles (
    id            UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id       UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    birth_date    DATE,
    height        DOUBLE PRECISION,
    gender        VARCHAR(10),
    units         VARCHAR(30)
);

-- Body measurements
CREATE TABLE body_measurements (
    id              UUID             NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_profile_id UUID             NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    creation_date   TIMESTAMPTZ      NOT NULL DEFAULT now(),
    date_taken      DATE,
    weight          DOUBLE PRECISION,
    waist           DOUBLE PRECISION
);

-- Sleep logs
CREATE TABLE sleep_logs (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_profile_id UUID        NOT NULL REFERENCES user_profiles (id) ON DELETE CASCADE,
    creation_date   TIMESTAMPTZ NOT NULL DEFAULT now(),
    bed_time        TIMESTAMPTZ,
    wake_time       TIMESTAMPTZ
);

-- Workout plans
CREATE TABLE workout_plans (
    id      UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    name    VARCHAR(255) NOT NULL
);

-- Workouts
CREATE TABLE workouts (
    id               UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    workout_plan_id  UUID         NOT NULL REFERENCES workout_plans (id) ON DELETE CASCADE,
    name             VARCHAR(255) NOT NULL,
    sets             INT          NOT NULL DEFAULT 0,
    reps_min         INT          NOT NULL DEFAULT 0,
    reps_max         INT          NOT NULL DEFAULT 0,
    rest_seconds     INT          NOT NULL DEFAULT 0,
    sort_order       INT          NOT NULL DEFAULT 0
);

-- Workout sessions
CREATE TABLE workout_sessions (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    workout_plan_id UUID        NOT NULL REFERENCES workout_plans (id),
    started_at      TIMESTAMPTZ,
    finished_at     TIMESTAMPTZ,
    deload          BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Workout tracks
CREATE TABLE workout_tracks (
    id         UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES workout_sessions (id) ON DELETE CASCADE,
    workout_id UUID NOT NULL REFERENCES workouts (id),
    sets       INT  NOT NULL DEFAULT 0,
    reps       INT  NOT NULL DEFAULT 0,
    weight     DOUBLE PRECISION NOT NULL DEFAULT 0
);

-- Default superadmin user (password: muta55DJ)
INSERT INTO users (username, email, password, first_name, last_name, created_at, updated_at)
VALUES ('jernej', 'jernej.klancic@gmail.com', '$2a$10$Cqpfw5l5VBcXmUD9.FDQruSoxB39OXkKbXOHiGN8e8kolaPUKf/m.', 'Jernej', '', NOW(), NOW());

INSERT INTO user_roles (user_id, role)
SELECT id, 'SUPERADMIN' FROM users WHERE username = 'jernej';
