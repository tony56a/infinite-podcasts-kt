package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.repos.models.ScriptType
import java.time.OffsetDateTime
import java.util.*

data class ScriptModel(
    val id: UUID?,
    val requestId: UUID,
    val requestedAt: OffsetDateTime?,
    val fulfilledAt: OffsetDateTime?,
    val topic: String,
    val scriptType: ScriptType,
    val requestingUser: UserModel,
    val status: ScriptStatus?,
    val characters: Map<String, ScriptGuestCharacterModel>? = null,
    val scriptLines: List<ScriptContentLineModel>? = null,
)