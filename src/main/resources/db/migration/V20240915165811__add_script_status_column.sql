ALTER TABLE scripts ADD status VARCHAR(100);

CREATE INDEX idx_scripts_status ON scripts (status);