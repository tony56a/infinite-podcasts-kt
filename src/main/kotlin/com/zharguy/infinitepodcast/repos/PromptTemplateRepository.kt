package com.zharguy.infinitepodcast.repos

import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.PromptTemplateDataModel
import com.zharguy.infinitepodcast.repos.models.ScriptType
import com.zharguy.infinitepodcast.repos.tables.PromptTemplates
import com.zharguy.infinitepodcast.repos.tables.Users
import com.zharguy.infinitepodcast.repos.tables.toPromptTemplateDataModel
import com.zharguy.infinitepodcast.repos.tables.toUserDataModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Singleton
class PromptTemplateRepository {

    @Inject
    lateinit var db: Database

    fun createPromptTemplate(promptTemplate: PromptTemplateDataModel): PromptTemplateDataModel {
        val existingEntry =
            doRetrievePromptTemplateByLlmModelAndScriptType(promptTemplate.llmModel, promptTemplate.scriptType)

        val idToReturn = PromptTemplates.insertAndGetId {
            it[createdAt] = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
            it[requestingUser] = EntityID(requireNotNull(promptTemplate.requestingUser.id), Users)
            it[systemText] = promptTemplate.systemText
            it[promptTemplateText] = promptTemplate.promptTemplateText
            it[scriptType] = promptTemplate.scriptType
            it[llmModel] = promptTemplate.llmModel
            it[revision] = existingEntry?.revision?.plus(1) ?: 1
            it[active] = promptTemplate.active
        }

        return retrivePromptTemplateById(idToReturn.value)
    }

    fun retrivePromptTemplateById(promptTemplateId: UUID): PromptTemplateDataModel {
        return (PromptTemplates innerJoin Users).selectAll().where { (PromptTemplates.id eq promptTemplateId) }
            .forUpdate()
            .map {
                val user = it.toUserDataModel()
                it.toPromptTemplateDataModel(user)
            }
            .single()
    }

    fun retrievePromptTemplateByLlmModelAndScriptType(
        llmModel: LlmModel,
        scriptType: ScriptType
    ): PromptTemplateDataModel {
        return doRetrievePromptTemplateByLlmModelAndScriptType(
            llmModel = llmModel,
            scriptType = scriptType
        )
            ?: throw IllegalArgumentException("Not found for extId and source")
    }

    private fun doRetrievePromptTemplateByLlmModelAndScriptType(
        llmModel: LlmModel,
        scriptType: ScriptType
    ): PromptTemplateDataModel? {
        val version = PromptTemplates.revision.max().alias("max_revision")

        val promptTemplateMaxQuery = PromptTemplates
            .select(
                PromptTemplates.llmModel,
                PromptTemplates.scriptType,
                version,
            ).groupBy(PromptTemplates.llmModel, PromptTemplates.scriptType)
            .alias("right_side")

        return (PromptTemplates innerJoin Users).join(
            promptTemplateMaxQuery, JoinType.INNER
        ) {
            (PromptTemplates.llmModel eq promptTemplateMaxQuery[PromptTemplates.llmModel]) and
                    (PromptTemplates.scriptType eq promptTemplateMaxQuery[PromptTemplates.scriptType])
            (PromptTemplates.revision eq promptTemplateMaxQuery[version])
        }.selectAll().where {
            (PromptTemplates.llmModel eq llmModel) and
                    (PromptTemplates.scriptType eq scriptType) and
                    PromptTemplates.active
        }.map {
            val user = it.toUserDataModel()
            it.toPromptTemplateDataModel(user)
        }.singleOrNull()
    }
}