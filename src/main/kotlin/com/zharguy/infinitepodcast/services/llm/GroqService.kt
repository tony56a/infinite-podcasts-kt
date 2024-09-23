package com.zharguy.infinitepodcast.services.llm

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.zharguy.infinitepodcast.clients.GroqClient
import com.zharguy.infinitepodcast.clients.models.groq.ChatCompletionModel
import com.zharguy.infinitepodcast.clients.models.groq.ChatCompletionRequest
import com.zharguy.infinitepodcast.clients.models.groq.SystemChatCompletionMessage
import com.zharguy.infinitepodcast.clients.models.groq.UserChatCompletionMessage
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import java.util.*

@Singleton
class GroqService : LlmService {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(GroqService::class.java)
    }

    val model: ChatCompletionModel = ChatCompletionModel.LLAMA31_70B

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var client: GroqClient

    override suspend fun performInference(
        inputScript: ScriptModel,
        systemMessage: String,
        promptMessage: String
    ): Pair<List<ScriptContentLineModel>, List<ScriptGuestCharacterModel>> {
        val request = ChatCompletionRequest(
            model = model,
            messages = listOf(
                SystemChatCompletionMessage(systemMessage),
                UserChatCompletionMessage(promptMessage)
            ),
            temperature = 1.0,
            maxTokens = 8000,
            user = UUID.randomUUID()
        )
        val completions = client.chatCompletion(request)
        val rawScript = completions.choices.single().message.content
        return try {
            val character = objectMapper.readValue<ScriptGuestCharacterModel>(rawScript)
            val node =
                objectMapper.readTree(rawScript).get("lines") ?: throw JsonMappingException("lines are not found")
            val lines = objectMapper.treeToValue<List<ScriptContentLineModel>>(node)
            Pair(lines, listOf(character))
        } catch (e: JsonMappingException) {
            logger.error(
                "encountered exception when parsing LLM response",
                kv("script_id", inputScript.id),
                kv("exception", e.message)
            )
            throw RuntimeException(e)
        }
    }
}