package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.llm.LlmService
import com.zharguy.infinitepodcast.services.models.ScriptModel
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ScriptGenService {

    val prompt: String = """
        create a JSON code block without any other comments or text at least 1500 tokens long,
        containing a podcast script of an episode of the Poe Reagan show in the style of the Joe Rogan experience,
        between a male podcast host named Poe Reagan, and a %s character that is %s, with %s
        Do not talk, do not say anything else other then the JSON block.
        
        Topic: '%s'
    """.trimIndent()

    val systemMessage: String = """
        You are only capable of returning JSON responses in the following format:
        
        { \"guest_name\": \"guest name\", \"guest_type\": \"normal, robot, skeleton\", \"guest_speaker_voice_type\": \"male or female\", \"lines\": [ {\"speaker\": \"poe or guest\", \"content\": \"text of the speaker\"} ] }
    """.trimIndent()

    val randomVoiceTypeStr: String = "male or female"

    val randomName: String = "an actual random name"


    @Inject
    lateinit var groqService: LlmService

    suspend fun performInference(request: ScriptModel): ScriptModel {
        val llmService = selectLlmClient(request)

        val (generatedLines, characters) = llmService.performInference(
            request,
            systemMessage = systemMessage,
            promptMessage = generatePrompt(request),
        )
        return request.copy(
            scriptLines = generatedLines,
            characters = characters,
            status = ScriptStatus.GENERATED
        )
    }

    private fun selectLlmClient(request: ScriptModel): LlmService {
        return groqService
    }

    private fun generatePrompt(request: ScriptModel): String {
        // Get character info
        val guestCharacter = requireNotNull(request.characters).single()
        val guestTypeStr = (guestCharacter.characterType ?: CharacterType.NORMAL).toString().lowercase()
        val guestVoiceTypeStr = guestCharacter.speakerVoiceType?.toString()?.lowercase() ?: randomVoiceTypeStr
        val guestNameStr = guestCharacter.name?.let { name ->
            "the name $name"
        } ?: randomName
        
        return prompt.format(
            guestTypeStr,
            guestVoiceTypeStr,
            guestNameStr,
            request.topic
        )
    }
}