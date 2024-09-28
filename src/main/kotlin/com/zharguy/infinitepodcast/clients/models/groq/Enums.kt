package com.zharguy.infinitepodcast.clients.models.groq

import com.fasterxml.jackson.annotation.JsonValue
import io.micronaut.serde.annotation.Serdeable

@Serdeable
enum class ChatCompletionRole(@JsonValue val value: String) {
    ASSISTANT("assistant"),
    SYSTEM("system"),
    USER("user")
}

@Serdeable
enum class ChatCompletionModel(@JsonValue val value: String) {
    LLAMA32_90B("llama-3.2-90b-text-preview"),
    LLAMA31_70B("llama-3.1-70b-versatile"),
    LLAMA31_8B("llama-3.1-8b-instant"),
    LLAMA3_70B("llama3-70b-8192"),
    LLAMA3_8B("llama3-8b-8192"),
}