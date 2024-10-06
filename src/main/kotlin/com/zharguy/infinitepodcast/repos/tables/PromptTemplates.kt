package com.zharguy.infinitepodcast.repos.tables

import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.PromptTemplateDataModel
import com.zharguy.infinitepodcast.repos.models.ScriptType
import com.zharguy.infinitepodcast.repos.models.UserDataModel
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone
import java.time.OffsetDateTime
import java.util.*

object PromptTemplates : UUIDTable("prompt_templates") {
    val createdAt: Column<OffsetDateTime> = timestampWithTimeZone("created_at")
    val requestingUser: Column<EntityID<UUID>> = reference("requesting_user", Users)
    val systemText: Column<String> = text("system_text")
    val promptTemplateText: Column<String> = text("prompt_template")
    val scriptType: Column<ScriptType> = enumerationByName("script_type", 100)
    val llmModel: Column<LlmModel> = enumerationByName("llm_model", 100)
    val revision: Column<Int> = integer("revision")
    val active: Column<Boolean> = bool("active")

    init {
        index(true, llmModel, scriptType, revision) // case 1 - Unique index
    }
}

fun ResultRow.toPromptTemplateDataModel(requestingUser: UserDataModel): PromptTemplateDataModel {
    return PromptTemplateDataModel(
        id = this[PromptTemplates.id].value,
        createdAt = this[PromptTemplates.createdAt],
        requestingUser = requestingUser,
        systemText = this[PromptTemplates.systemText],
        promptTemplateText = this[PromptTemplates.promptTemplateText],
        scriptType = this[PromptTemplates.scriptType],
        llmModel = this[PromptTemplates.llmModel],
        revision = this[PromptTemplates.revision],
        active = this[PromptTemplates.active],
    )
}
