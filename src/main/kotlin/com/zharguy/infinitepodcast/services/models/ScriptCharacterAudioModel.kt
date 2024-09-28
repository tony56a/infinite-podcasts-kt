package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.AudioSource

data class ScriptCharacterAudioModel(
    val voiceType: String,
    val voiceSpeedMultiplier: Double,
    val audioSource: AudioSource
)
