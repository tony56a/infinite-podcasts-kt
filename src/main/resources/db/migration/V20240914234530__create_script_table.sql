CREATE TABLE IF NOT EXISTS scripts
(
    id VARCHAR PRIMARY KEY,
    requested_at TIMESTAMP NOT NULL,
    fulfilled_at TIMESTAMP,
    requesting_user VARCHAR NOT NULL,
    topic VARCHAR,
    script_type VARCHAR,
    characters JSONB,
    script_lines JSONB
);

ALTER TABLE scripts
    ADD CONSTRAINT fk_scripts_requesting_user FOREIGN KEY (requesting_user) REFERENCES users (id);

