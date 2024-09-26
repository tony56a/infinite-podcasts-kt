package com.zharguy.infinitepodcast.services.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType

data class ScriptGuestCharacterModel(
    @JsonProperty("guest_name")
    val name: String?,
    @JsonProperty("guest_type")
    val characterType: CharacterType?,
    @JsonProperty("guest_speaker_voice_type")
    val speakerVoiceType: SpeakerVoiceType?
)
