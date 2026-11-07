CREATE TABLE IF NOT EXISTS tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    value VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_token_value_active ON tokens (value);