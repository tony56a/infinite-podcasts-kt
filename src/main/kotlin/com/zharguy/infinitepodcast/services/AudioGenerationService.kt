package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.AudioSource
import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType
import com.zharguy.infinitepodcast.services.audio.MimicService
import com.zharguy.infinitepodcast.services.audio.OpenedAiService
import com.zharguy.infinitepodcast.services.audio.TtsService
import com.zharguy.infinitepodcast.services.models.ScriptCharacterAudioModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory


@Singleton
class AudioGenerationService {
    companion object {
        private val logger = LoggerFactory.getLogger(AudioGenerationService::class.java)
    }

    val maleVoices: List<ScriptCharacterAudioModel> = listOf(
        ScriptCharacterAudioModel(
            voiceType = "p259",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p279",
            voiceSpeedMultiplier = 0.65,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p247",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p263",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p274",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p360",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
    )

    val femaleVoices: List<ScriptCharacterAudioModel> = listOf(
        ScriptCharacterAudioModel(
            voiceType = "p299",
            voiceSpeedMultiplier = 0.65,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p236",
            voiceSpeedMultiplier = 0.715,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p250",
            voiceSpeedMultiplier = 0.65,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p261",
            voiceSpeedMultiplier = 0.75,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p283",
            voiceSpeedMultiplier = 0.715,
            audioSource = AudioSource.OPENEDAI
        ),
        ScriptCharacterAudioModel(
            voiceType = "p361",
            voiceSpeedMultiplier = 0.8,
            audioSource = AudioSource.OPENEDAI
        ),
    )

    @Inject
    lateinit var mimicService: MimicService

    @Inject
    lateinit var openedAiService: OpenedAiService

    suspend fun generateAudioForScript(script: ScriptModel): ScriptModel {
        logger.info("Generating audio for script", *script.getLoggerArgs())

        val characterVoiceMapping = script.characterVoiceMapping?.let {
            logger.info("Reusing voice mapping for script", *script.getLoggerArgs())
            script.characterVoiceMapping
        } ?: getCharacterVoiceMapping(script)

        val scriptLinesByTtsService = requireNotNull(script.scriptLines).groupBy { scriptLine ->
            getTtsVoiceService(characterVoiceMapping.getValue(scriptLine.speaker))
        }

        val scriptLineAudio = scriptLinesByTtsService.entries.map { (service, linesByService) ->
            service.generateAudio(linesByService, characterVoiceMapping)
        }.flatMap { map -> map.entries }
            .associate(Map.Entry<ScriptContentLineModel, ByteArray>::toPair)

        if (scriptLineAudio.isEmpty()) {
            logger.error("Audio was not generated for the script", *script.getLoggerArgs())
            throw RuntimeException("Audio was not generated")
        }

        return script.copy(
            characterVoiceMapping = characterVoiceMapping,
            scriptLineAudio = scriptLineAudio
        )
    }

    private fun getCharacterVoiceMapping(script: ScriptModel): Map<String, ScriptCharacterAudioModel> {
        val retVal = mutableMapOf<String, ScriptCharacterAudioModel>()
        val hostName = "poe"
        val guestNames = requireNotNull(script.characters).map { character ->
            Pair(character, requireNotNull(character.name).trim().lowercase().split(" "))
        }
        val scriptLineCharacters = requireNotNull(script.scriptLines).map {
            it.speaker
        }.distinct()

        for (scriptLineCharacter in scriptLineCharacters) {
            val trimmedCharacterName = scriptLineCharacter.trim().lowercase()

            for ((guest, guestName) in guestNames) {
                if (guestName.any { component -> trimmedCharacterName.contains(component) } ||
                    trimmedCharacterName.contains("guest")) {
                    retVal[scriptLineCharacter] = getGuestVoice(guest)
                }
            }

            if (trimmedCharacterName.contains("host") ||
                trimmedCharacterName.contains(hostName)
            ) {
                retVal[scriptLineCharacter] = getHostVoice()
            }
        }

        return retVal
    }

    private fun getTtsVoiceService(scriptCharacterAudioModel: ScriptCharacterAudioModel): TtsService {
        return when (scriptCharacterAudioModel.audioSource) {
            AudioSource.MIMIC3 -> mimicService
            AudioSource.OPENEDAI -> openedAiService
        }
    }

    private fun getHostVoice(): ScriptCharacterAudioModel {
        return ScriptCharacterAudioModel(
            voiceType = "jwk",
            voiceSpeedMultiplier = 0.85,
            audioSource = AudioSource.OPENEDAI
        )
    }

    private fun getGuestVoice(guestCharacter: ScriptGuestCharacterModel): ScriptCharacterAudioModel {
        if (guestCharacter.characterType == CharacterType.ROBOT) {
            return ScriptCharacterAudioModel(
                voiceType = "fem",
                voiceSpeedMultiplier = 0.9,
                audioSource = AudioSource.OPENEDAI
            )
        }

        return when (guestCharacter.speakerVoiceType) {
            SpeakerVoiceType.MALE -> maleVoices.random()
            SpeakerVoiceType.FEMALE -> femaleVoices.random()
            else -> throw IllegalArgumentException()
        }
    }
}