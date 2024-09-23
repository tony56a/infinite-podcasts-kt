package com.zharguy.infinitepodcast.clients.models.groq

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.micronaut.serde.annotation.Serdeable
import java.util.*

@Serdeable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatCompletionRequest(
    val model: ChatCompletionModel,
    val messages: List<ChatCompletionMessage>,
    val temperature: Double,
    val maxTokens: Int,
    val user: UUID,
) {
    val stream: Boolean = false
}
