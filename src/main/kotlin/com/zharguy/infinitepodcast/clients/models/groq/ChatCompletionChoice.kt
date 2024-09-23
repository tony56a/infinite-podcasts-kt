package com.zharguy.infinitepodcast.clients.models.groq

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatCompletionChoice(
    val index: Int,
    val message: AssistantChatCompletionMessage,
    val finishReason: String,
)
