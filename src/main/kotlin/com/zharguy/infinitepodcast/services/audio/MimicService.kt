package com.zharguy.infinitepodcast.services.audio

import com.zharguy.infinitepodcast.clients.MimicClient
import com.zharguy.infinitepodcast.services.models.ScriptCharacterAudioModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import io.micronaut.http.client.exceptions.HttpClientException
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory

@Singleton
class MimicService : TtsService {
    companion object {
        private val logger = LoggerFactory.getLogger(MimicService::class.java)
    }

    @Inject
    lateinit var mimicClient: MimicClient

    override suspend fun generateAudio(
        scriptLines: List<ScriptContentLineModel>,
        characterVoiceMap: Map<String, ScriptCharacterAudioModel>
    ): Map<ScriptContentLineModel, ByteArray> {

        return try {
            coroutineScope {
                scriptLines.map { scriptLine ->
                    val speakerVoiceInfo = characterVoiceMap.getValue(scriptLine.speaker)
                    async(Dispatchers.IO) {
                        logger.info("Making request for line", kv("line", scriptLine.content))
                        val audio = mimicClient.chatCompletion(
                            voice = speakerVoiceInfo.voiceType, lengthScale = speakerVoiceInfo.voiceSpeedMultiplier,
                            request = scriptLine.content
                        )
                        Pair(scriptLine, audio)
                    }
                }.awaitAll().toMap() ?: emptyMap()
            }

        } catch (e: HttpClientException) {
            logger.error("error during generation, returning empty list instead")
            emptyMap()
        }
    }
}