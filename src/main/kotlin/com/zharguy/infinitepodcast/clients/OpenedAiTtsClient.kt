package com.zharguy.infinitepodcast.clients

import com.zharguy.infinitepodcast.clients.models.openedai.OpenedAiRequest
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable

@Client(value = "openedai")
interface OpenedAiTtsClient {

    @Retryable(attempts = "3")
    @Post("v1/audio/speech")
    @SingleResult
    fun chatCompletion(
        @Body request: OpenedAiRequest
    ): ByteArray
}