package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType

data class ScriptGuestCharacterModel(
    val name: String,
    val characterType: CharacterType,
    val speakerVoiceType: SpeakerVoiceType
)
