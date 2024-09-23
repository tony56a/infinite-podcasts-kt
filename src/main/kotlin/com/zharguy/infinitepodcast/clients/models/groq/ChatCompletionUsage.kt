package com.zharguy.infinitepodcast.clients.models.groq

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatCompletionUsage(
    val queueTime: Double,
    val promptTokens: Int,
    val promptTime: Double,
    val completionTokens: Int,
    val completionTime: Double,
    val totalTokens: Int,
    val totalTime: Double
)
