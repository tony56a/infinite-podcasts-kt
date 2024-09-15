CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_scripts_topic
ON scripts
USING gin (topic gin_trgm_ops);