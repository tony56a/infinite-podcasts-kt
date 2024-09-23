package com.zharguy.infinitepodcast.clients.models.groq

import io.micronaut.serde.annotation.Serdeable

@Serdeable
sealed class ChatCompletionMessage(val role: ChatCompletionRole) {
    abstract val content: String
}

@Serdeable
data class UserChatCompletionMessage(override val content: String) : ChatCompletionMessage(ChatCompletionRole.USER)
@Serdeable
data class AssistantChatCompletionMessage(override val content: String) : ChatCompletionMessage(ChatCompletionRole.SYSTEM)
@Serdeable
data class SystemChatCompletionMessage(override val content: String) : ChatCompletionMessage(ChatCompletionRole.ASSISTANT)

