package com.zharguy.infinitepodcast.services.audio

import com.zharguy.infinitepodcast.clients.MimicClient
import com.zharguy.infinitepodcast.services.models.ScriptCharacterAudioModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.http.client.exceptions.HttpClientException
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import net.logstash.logback.argument.StructuredArguments.kv

private val logger = KotlinLogging.logger {}

@Singleton
@Named("mimic")
class MimicService : TtsService {

    @Inject
    lateinit var mimicClient: MimicClient

    override suspend fun generateAudio(
        scriptLines: List<ScriptContentLineModel>,
        characterVoiceMap: Map<String, ScriptCharacterAudioModel>
    ): Map<ScriptContentLineModel, ByteArray> {
        return try {
            scriptLines.toSet().chunked(4).asFlow().map { chunk ->
                coroutineScope {
                    chunk.map { scriptLine ->
                        val speakerVoiceInfo = characterVoiceMap.getValue(scriptLine.speaker)
                        async(Dispatchers.IO) {
                            logger.info("Making request for line", kv("line", scriptLine.content))
                            val audio = mimicClient.chatCompletion(
                                voice = speakerVoiceInfo.voiceType, lengthScale = speakerVoiceInfo.voiceSpeedMultiplier,
                                request = scriptLine.content
                            )
                            Pair(scriptLine, audio)
                        }
                    }.awaitAll().toMap()
                }
            }.toList().flatMap { it.entries }
                .associate { it.key to it.value }


        } catch (e: HttpClientException) {
            logger.error("Error during generation, returning empty list instead", e)
            emptyMap()
        }
    }
}