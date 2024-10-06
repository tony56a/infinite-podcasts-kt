package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.ScriptType
import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments.kv
import java.time.OffsetDateTime
import java.util.*

data class PromptTemplateModel(
    val id: UUID?,
    val createdAt: OffsetDateTime?,
    val requestingUser: UserModel,
    val systemText: String,
    val promptTemplateText: String,
    val scriptType: ScriptType,
    val llmModel: LlmModel,
    val revision: Int?,
    val active: Boolean
) {
    fun getLogArgs(): Array<StructuredArgument> {
        return arrayOf(
            kv("llm_model", llmModel.name),
            kv("script_type", scriptType.name)
        )
    }
}
