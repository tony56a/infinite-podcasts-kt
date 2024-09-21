CREATE TABLE IF NOT EXISTS scripts
(
    id UUID PRIMARY KEY,
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    fulfilled_at TIMESTAMP WITH TIME ZONE,
    requesting_user UUID NOT NULL,
    topic TEXT,
    script_type VARCHAR(255),
    characters JSONB,
    script_lines JSONB
);

ALTER TABLE scripts
    ADD CONSTRAINT fk_scripts_requesting_user FOREIGN KEY (requesting_user) REFERENCES users (id);

