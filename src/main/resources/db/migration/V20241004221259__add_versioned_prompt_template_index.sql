CREATE UNIQUE INDEX prompt_templates_llm_model_script_type_revision_key
ON prompt_templates (llm_model, script_type, revision);

DROP INDEX IF EXISTS prompt_templates_llm_model_script_type_key;
