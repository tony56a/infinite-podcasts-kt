package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.llm.LlmService
import com.zharguy.infinitepodcast.services.models.PromptTemplateModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.commons.text.StringSubstitutor

private const val RANDOM_VOICE_TYPE_STR = "male or female"

private const val RANDOM_NAME_STR = "a random and absurd name"

private val logger = KotlinLogging.logger {}

@Singleton
class ScriptGenService {

    @Inject
    lateinit var promptTemplateService: PromptTemplateService

    @Inject
    lateinit var groqService: LlmService

    suspend fun performInference(request: ScriptModel): ScriptModel {
        val (llmService, llmModel) = selectLlmClient(request)
        val promptTemplate = promptTemplateService.retrievePromptTemplateByLlmModelScriptType(
            llmModel, request.scriptType
        )
        logger.info("Generating script", *request.getLoggerArgs())

        val (generatedLines, characters) = llmService.performInference(
            request,
            systemMessage = promptTemplate.systemText.trimIndent(),
            promptMessage = generatePrompt(request = request, promptTemplate = promptTemplate),
            llmModel = llmModel
        )
        return request.copy(
            scriptLines = generatedLines,
            characters = characters,
            status = ScriptStatus.GENERATED,
            promptTemplateId = promptTemplate.id
        )
    }

    private fun selectLlmClient(request: ScriptModel): Pair<LlmService, LlmModel> {
        return Pair(groqService, LlmModel.LLAMA31_70B)
    }

    private fun generatePrompt(request: ScriptModel, promptTemplate: PromptTemplateModel): String {
        // Get character info
        val guestCharacter = requireNotNull(request.characters).single()
        val guestTypeStr = (guestCharacter.characterType ?: CharacterType.NORMAL).toString().lowercase()
        val guestVoiceTypeStr = guestCharacter.speakerVoiceType?.toString()?.lowercase() ?: RANDOM_VOICE_TYPE_STR
        val guestNameStr = guestCharacter.name?.let { name ->
            "the name $name"
        } ?: RANDOM_NAME_STR

        val scriptAttributeMap = mapOf(
            "type" to guestTypeStr,
            "voiceType" to guestVoiceTypeStr,
            "name" to guestNameStr,
            "topic" to request.topic
        )
        return StringSubstitutor.replace(
            promptTemplate.promptTemplateText.trimIndent(), scriptAttributeMap, "#{", "}"
        )
    }
}