CREATE TABLE IF NOT EXISTS prompt_templates
(
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    requesting_user UUID NOT NULL,
    system_text TEXT NOT NULL,
    prompt_template TEXT NOT NULL,
    script_type VARCHAR NOT NULL,
    llm_model VARCHAR NOT NULL,
    revision INT NOT NULL,
    active BOOLEAN NOT NULL
);

ALTER TABLE prompt_templates
    ADD CONSTRAINT fk_prompt_templates_requesting_user
    FOREIGN KEY (requesting_user)
    REFERENCES users (id);

CREATE UNIQUE INDEX prompt_templates_llm_model_script_type_key
ON prompt_templates (llm_model, script_type);
