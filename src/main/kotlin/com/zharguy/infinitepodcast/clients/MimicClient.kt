package com.zharguy.infinitepodcast.clients

import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client(value = "mimic")
interface MimicClient {
    @Post("/api/tts")
    @SingleResult
    fun chatCompletion(
        @QueryValue voice: String,
        @QueryValue lengthScale: Double,
        @Body request: String
    ): ByteArray
}