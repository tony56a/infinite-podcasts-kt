package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.PromptTemplateRepository
import com.zharguy.infinitepodcast.repos.dbQuery
import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.ScriptType
import com.zharguy.infinitepodcast.services.mappers.fromDataModel
import com.zharguy.infinitepodcast.services.mappers.toDataModel
import com.zharguy.infinitepodcast.services.models.PromptTemplateModel
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class PromptTemplateService {

    @Inject
    lateinit var promptTemplateRepository: PromptTemplateRepository

    suspend fun createPromptTemplate(promptTemplateModel: PromptTemplateModel): PromptTemplateModel {
        val promptTemplateDataModel = dbQuery {

            promptTemplateRepository.createPromptTemplate(promptTemplateModel.toDataModel())
        }
        return promptTemplateDataModel.fromDataModel()
    }

    suspend fun retrievePromptTemplateByLlmModelScriptType(
        llmModel: LlmModel,
        scriptType: ScriptType
    ): PromptTemplateModel {
        val promptTemplateDataModel = dbQuery {
            promptTemplateRepository.retrievePromptTemplateByLlmModelAndScriptType(
                llmModel = llmModel, scriptType = scriptType
            )
        }
        return promptTemplateDataModel.fromDataModel()
    }
}