package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.llm.LlmService
import com.zharguy.infinitepodcast.services.models.ScriptModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.commons.text.StringSubstitutor
import org.slf4j.LoggerFactory

// TODO: Replace with versioned DB

private const val PROMPT = """
        create a JSON code block without any other comments or text at least 3000 tokens long,
        containing a podcast script of an episode of the Poe Reagan show in the style of the Joe Rogan experience,
        between a male podcast host named Poe Reagan, and a #{type} character that is #{voiceType}, with #{name}
        Do not talk, do not say anything else other then the JSON block.
        
        Topic: '#{topic}'
    """

private const val SYSTEM_MESSAGE = """
        You are only capable of returning JSON responses in the following format:
        
        { \"guest_name\": \"guest name\", \"guest_type\": \"normal, robot, skeleton\", \"guest_speaker_voice_type\": \"male or female\", \"lines\": [ {\"speaker\": \"poe or guest\", \"content\": \"text of the speaker\"} ] }
    """

private const val RANDOM_VOICE_TYPE_STR = "male or female"

private const val RANDOM_NAME_STR = "a random and absurd name"

@Singleton
class ScriptGenService {
    companion object {
        private val logger = LoggerFactory.getLogger(ScriptGenService::class.java)
    }

    @Inject
    lateinit var groqService: LlmService

    suspend fun performInference(request: ScriptModel): ScriptModel {
        val (llmService, llmModel) = selectLlmClient(request)
        logger.info("Generating script", *request.getLoggerArgs())
        val (generatedLines, characters) = llmService.performInference(
            request,
            systemMessage = SYSTEM_MESSAGE.trimIndent(),
            promptMessage = generatePrompt(request),
            llmModel = llmModel
        )
        return request.copy(
            scriptLines = generatedLines,
            characters = characters,
            status = ScriptStatus.GENERATED
        )
    }

    private fun selectLlmClient(request: ScriptModel): Pair<LlmService, LlmModel> {
        return Pair(groqService, LlmModel.LLAMA31_70B)
    }

    private fun generatePrompt(request: ScriptModel): String {
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
        logger.debug(
            StringSubstitutor.replace(
                PROMPT.trimIndent(), scriptAttributeMap, "#{", "}"
            )
        )
        return StringSubstitutor.replace(
            PROMPT.trimIndent(), scriptAttributeMap, "#{", "}"
        )
    }
}