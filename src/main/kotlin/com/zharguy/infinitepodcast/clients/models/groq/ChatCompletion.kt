package com.zharguy.infinitepodcast.clients.models.groq

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.micronaut.serde.annotation.Serdeable
import java.time.OffsetDateTime

@Serdeable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class ChatCompletion(
    val id: String,
    @JsonFormat(shape=JsonFormat.Shape.NUMBER, pattern="s")
    @JsonProperty("created")
    val createdDate: OffsetDateTime,
    val model: ChatCompletionModel,
    val choices: List<ChatCompletionChoice>,
    val usage: ChatCompletionUsage
)
