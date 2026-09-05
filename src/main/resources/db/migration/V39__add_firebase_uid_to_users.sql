-- V39: adicionar coluna firebase_uid e tabela user_skills para migração JWT → Firebase Auth
-- Firebase UIDs têm no máximo 28 caracteres alfanuméricos.

-- Coluna para vincular o usuário PostgreSQL à conta Firebase Auth.
ALTER TABLE platform.users
    ADD COLUMN firebase_uid VARCHAR(28) UNIQUE;

-- Tabela para skills do usuário (athlete, coach, referee, manager).
-- Mapeada via @ElementCollection em UserEntity.skills.
CREATE TABLE platform.user_skills (
    user_id UUID NOT NULL REFERENCES platform.users(id) ON DELETE CASCADE,
    skill   VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, skill)
);

CREATE INDEX idx_user_skills_user_id ON platform.user_skills(user_id);
