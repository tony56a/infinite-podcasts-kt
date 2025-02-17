package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.repos.models.ScriptType
import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments.kv
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
    val characters: List<ScriptGuestCharacterModel>? = null,
    val scriptLines: List<ScriptContentLineModel>? = null,
    val characterVoiceMapping: Map<String, ScriptCharacterAudioModel>? = null,
    val scriptLineAudio: Map<ScriptContentLineModel, ByteArray>? = null,
    val promptTemplateId: UUID? = null,
) {
    fun getLoggerArgs(): Array<StructuredArgument> = arrayOf(
        kv("script_id", this.id),
        kv("request_id", this.requestId),
    )
}