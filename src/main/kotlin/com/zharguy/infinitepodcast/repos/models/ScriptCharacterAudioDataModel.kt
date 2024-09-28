package com.zharguy.infinitepodcast.repos.models

data class ScriptCharacterAudioDataModel(
    val voiceType: String,
    val voiceSpeedMultiplier: Double,
    val audioSource: AudioSource
)
