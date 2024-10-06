package com.zharguy.infinitepodcast.repos.models

import java.time.OffsetDateTime
import java.util.*

data class PromptTemplateDataModel(
    val id: UUID?,
    val createdAt: OffsetDateTime?,
    val requestingUser: UserDataModel,
    val systemText: String,
    val promptTemplateText: String,
    val scriptType: ScriptType,
    val llmModel: LlmModel,
    val revision: Int?,
    val active: Boolean
)
