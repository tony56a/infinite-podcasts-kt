CREATE TABLE IF NOT EXISTS users
(
    id UUID PRIMARY KEY,
    ext_id VARCHAR(1000) NOT NULL,
    user_source VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX users_ext_id_user_source_key ON users (ext_id, user_source);