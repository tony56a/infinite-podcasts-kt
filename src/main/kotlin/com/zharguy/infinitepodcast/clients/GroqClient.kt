package com.zharguy.infinitepodcast.clients

import com.zharguy.infinitepodcast.clients.models.groq.ChatCompletion
import com.zharguy.infinitepodcast.clients.models.groq.ChatCompletionRequest
import com.zharguy.infinitepodcast.configuration.GroqConfiguration
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable

@Client(id = "groq")
interface GroqClient {

    @Retryable(attempts = "3")
    @Post("/openai/v1/chat/completions")
    @SingleResult
    fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletion
}

@ClientFilter(Filter.MATCH_ALL_PATTERN, serviceId = ["groq"])
class GroqAuthFilter(private val configuration: GroqConfiguration) {

    @RequestFilter
    fun doFilter(request: MutableHttpRequest<*>) {
        request.bearerAuth(configuration.apiKey)
    }
}
