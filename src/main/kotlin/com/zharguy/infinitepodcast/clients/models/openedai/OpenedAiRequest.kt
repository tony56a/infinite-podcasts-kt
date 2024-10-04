package com.zharguy.infinitepodcast.clients.models.openedai

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.micronaut.serde.annotation.Serdeable

@Serdeable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OpenedAiRequest(
    val input: String,
    val voice: String,
    val speed: Double
) {
    val model = "tts-1"
    val responseFormat = "wav"
}