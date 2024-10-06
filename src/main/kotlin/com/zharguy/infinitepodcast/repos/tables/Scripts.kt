package com.zharguy.infinitepodcast.repos.tables

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zharguy.infinitepodcast.repos.models.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.json.jsonb
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone
import java.time.OffsetDateTime
import java.util.*

private val mapper = jacksonObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


object Scripts : UUIDTable("scripts") {
    val requestId: Column<UUID> = uuid("request_id")
    val requestedAt: Column<OffsetDateTime> = timestampWithTimeZone("requested_at")
    val fulfilledAt: Column<OffsetDateTime?> = timestampWithTimeZone("fulfilled_at").nullable()
    val requestingUser: Column<EntityID<UUID>> = reference("requesting_user", Users)
    val topic: Column<String> = text("topic").index("idx_scripts_topic")
    val scriptType: Column<ScriptType> = enumerationByName("script_type", 100)
    val status: Column<ScriptStatus> = enumerationByName("status", 100)
    val characters: Column<List<ScriptGuestCharacterDataModel>?> = jsonb("characters",
        { mapper.writeValueAsString(it) },
        { mapper.readValue<List<ScriptGuestCharacterDataModel>>(it) }).nullable()
    val scriptLines: Column<List<ScriptContentLineDataModel>?> = jsonb("script_lines",
        { mapper.writeValueAsString(it) },
        { mapper.readValue<List<ScriptContentLineDataModel>>(it) }).nullable()
    val characterVoiceMapping: Column<Map<String, ScriptCharacterAudioDataModel>?> = jsonb("script_audio_info",
        { mapper.writeValueAsString(it) },
        { mapper.readValue<Map<String, ScriptCharacterAudioDataModel>>(it) }).nullable()
    val promptTemplate: Column<EntityID<UUID>?> = reference("prompt_template_id", PromptTemplates).nullable()
}

fun ResultRow.toScriptDataModel(requestingUser: UserDataModel): ScriptDataModel {
    return ScriptDataModel(
        id = this[Scripts.id].value,
        requestedAt = this[Scripts.requestedAt],
        fulfilledAt = this[Scripts.fulfilledAt],
        requestingUser = requestingUser,
        topic = this[Scripts.topic],
        scriptType = this[Scripts.scriptType],
        characters = this[Scripts.characters],
        scriptLines = this[Scripts.scriptLines],
        requestId = this[Scripts.requestId],
        status = this[Scripts.status],
        characterVoiceMapping = this[Scripts.characterVoiceMapping]
    )
}
