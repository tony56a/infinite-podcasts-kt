CREATE TABLE IF NOT EXISTS users
(
    id VARCHAR PRIMARY KEY,
    ext_id VARCHAR NOT NULL,
    user_source VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX users_ext_id_user_source_key ON users (ext_id, user_source);