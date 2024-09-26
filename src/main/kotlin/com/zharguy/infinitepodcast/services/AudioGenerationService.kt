package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.clients.MimicClient
import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
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
class AudioGenerationService {

    companion object {
        private val logger = LoggerFactory.getLogger(AudioGenerationService::class.java)
    }

    @Inject
    lateinit var mimicClient: MimicClient

    suspend fun generateAudioForScript(script: ScriptModel): Map<ScriptContentLineModel, ByteArray> {
        val guestCharacter = requireNotNull(script.characters).first()
        val (guestVoice, guestVoiceSpeed) = getGuestVoice(guestCharacter)

        return try {
            coroutineScope {
                script.scriptLines?.map { scriptLine ->
                    async(Dispatchers.IO) {
                        logger.info("Making request for line", kv("line", scriptLine.content))
                        val audio = mimicClient.chatCompletion(
                            voice = guestVoice, lengthScale = guestVoiceSpeed, request =
                            scriptLine.content
                        )
                        Pair(scriptLine, audio)
                    }
                }?.awaitAll()?.toMap() ?: emptyMap()
            }

        } catch (e: HttpClientException) {
            logger.error("error during generation, returning empty list instead")
            emptyMap()
        }
    }

    private fun getGuestVoice(guestCharacter: ScriptGuestCharacterModel): Pair<String, Double> {
        if (guestCharacter.characterType == CharacterType.ROBOT) {
            return Pair("en_US/cmu-arctic_low#fem", 1.1)
        }

        return when (guestCharacter.speakerVoiceType) {
            SpeakerVoiceType.MALE -> Pair("en_US/vctk_low#p259", 1.2)
            SpeakerVoiceType.FEMALE -> Pair("en_US/vctk_low#p299", 1.33)
            else -> throw IllegalArgumentException()
        }
    }
}