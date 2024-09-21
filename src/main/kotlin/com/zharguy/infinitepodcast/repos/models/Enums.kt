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
    PENDING,
    GENERATED
}