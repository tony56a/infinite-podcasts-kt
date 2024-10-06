package com.zharguy.infinitepodcast.repos.models

import java.time.OffsetDateTime
import java.util.*

data class ScriptDataModel(
    val id: UUID? = null,
    val requestId: UUID,
    val requestedAt: OffsetDateTime?,
    val fulfilledAt: OffsetDateTime?,
    val topic: String,
    val scriptType: ScriptType,
    val requestingUser: UserDataModel,
    val status: ScriptStatus,
    val characters: List<ScriptGuestCharacterDataModel>? = null,
    val scriptLines: List<ScriptContentLineDataModel>? = null,
    val characterVoiceMapping: Map<String, ScriptCharacterAudioDataModel>? = null,
    val promptTemplateId: UUID? = null,
)
