ALTER TABLE scripts ADD prompt_template_id UUID;

ALTER TABLE scripts
ADD CONSTRAINT fk_scripts_prompt_template_id FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates (id);
