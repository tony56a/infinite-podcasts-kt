package com.zharguy.infinitepodcast.repos.models

enum class ScriptType {
    PODCAST,
    RAP_BATTLE,
    BUSINESS_TALK,
}

enum class ExtUserSource {
    ADMIN,
    DISCORD,
    TWITCH,
    AUTOMATION,
}

enum class CharacterType {
    NORMAL,
    SKELETON,
    ROBOT,
}

enum class SpeakerVoiceType {
    MALE,
    FEMALE,
}

enum class ScriptStatus {
    /** Contents haven't been generated yet for the script */
    PENDING,

    /** Contents have been generated, but script hasn't been published to the queue */
    GENERATED,

    /** Contents have been published to downstream queues */
    PUBLISHED,
}

enum class AudioSource {
    MIMIC3,
    OPENEDAI,
}

enum class LlmModel {
    LLAMA32_90B,
    LLAMA31_70B,
    LLAMA31_8B,
    LLAMA3_70B,
    LLAMA3_8B,
}