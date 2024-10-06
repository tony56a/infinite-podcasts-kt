insert
	into
	public.users
(id,
	ext_id,
	user_source,
	created_at)
values('a40b29dd-30b7-4d78-b4ea-164392bf3c25'::uuid,
'admin-user',
'ADMIN',
'2024-09-19 00:48:35.661') on
conflict do nothing;

insert
	into
	public.prompt_templates
(id,
	created_at,
	requesting_user,
	system_text,
	prompt_template,
	script_type,
	llm_model,
	revision,
	active)
values(gen_random_uuid(),
'2024-10-05 00:34:05.556',
'a40b29dd-30b7-4d78-b4ea-164392bf3c25'::uuid,
'You are only capable of returning JSON responses in the following format:
{ "guest_name": "guest name", "guest_type": "normal, robot, skeleton", "guest_speaker_voice_type": "male or female", "lines": [ {"speaker": "poe or guest", "content": "text of the speaker"} ] }',
'create a JSON code block without any other comments or text at least 3000 tokens long,
containing a podcast script of an episode of the Poe Reagan show in the style of the Joe Rogan experience,
between a male podcast host named Poe Reagan, and a #{type} character that is #{voiceType}, with #{name}
Do not talk, do not say anything else other then the JSON block.

Topic: ''#{topic}''',
'PODCAST',
'LLAMA31_70B',
1,
true) ON conflict DO NOTHING;

insert
	into
	public.prompt_templates
(id,
	created_at,
	requesting_user,
	system_text,
	prompt_template,
	script_type,
	llm_model,
	revision,
	active)
values(gen_random_uuid(),
'2024-10-05 21:19:02.570',
'a40b29dd-30b7-4d78-b4ea-164392bf3c25'::uuid,
'You are only capable of returning JSON responses in the following format:
{ "guest_name": "guest name", "guest_type": "normal, robot, skeleton", "guest_speaker_voice_type": "male or female", "lines": [ {"speaker": "poe or guest", "content": "text of the speaker"} ] }',
'create a JSON code block without any other comments or text at least 3000 tokens long,
containing a business speech by a business leader named Poe Reagan to his employees
Do not talk, do not say anything else other then the JSON block.

Topic: #{topic}',
'BUSINESS_TALK',
'LLAMA31_70B',
1,
true) ON conflict DO NOTHING;

insert
	into
	public.prompt_templates
(id,
	created_at,
	requesting_user,
	system_text,
	prompt_template,
	script_type,
	llm_model,
	revision,
	active)
values(gen_random_uuid(),
'2024-10-05 20:43:25.938',
'a40b29dd-30b7-4d78-b4ea-164392bf3c25'::uuid,
'You are only capable of returning JSON responses in the following format:
{ "guest_name": "guest name", "guest_type": "normal, robot, skeleton", "guest_speaker_voice_type": "male or female", "lines": [ {"speaker": "poe or guest", "content": "text of the speaker"} ] }',
'Create a JSON code block without any other comments or text at least 3000 tokens long, containing a rap battle script,
between a male podcast host named Poe Reagan, and a #{type} character that is #{voiceType}, with #{name}
Do not talk, do not say anything else other then the JSON block.

Topic: ''#{topic}''',
'RAP_BATTLE',
'LLAMA31_70B',
1,
true) ON conflict DO NOTHING;