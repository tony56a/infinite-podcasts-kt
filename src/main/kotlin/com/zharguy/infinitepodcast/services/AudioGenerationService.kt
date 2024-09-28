package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.models.AudioSource
import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType
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
            voiceType = "en_US/vctk_low#p259",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p279",
            voiceSpeedMultiplier = 1.55,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p247",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p263",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p274",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p360",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
    )

    val femaleVoices: List<ScriptCharacterAudioModel> = listOf(
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p299",
            voiceSpeedMultiplier = 1.55,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p236",
            voiceSpeedMultiplier = 1.4,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p250",
            voiceSpeedMultiplier = 1.55,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p261",
            voiceSpeedMultiplier = 1.2,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p283",
            voiceSpeedMultiplier = 1.4,
            audioSource = AudioSource.MIMIC3
        ),
        ScriptCharacterAudioModel(
            voiceType = "en_US/vctk_low#p361",
            voiceSpeedMultiplier = 1.25,
            audioSource = AudioSource.MIMIC3
        ),
    )

    @Inject
    lateinit var mimicService: TtsService

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

        if (scriptLineAudio.size != script.scriptLines.size) {
            logger.error("Audio was not generated for the script", *script.getLoggerArgs())
            throw IllegalStateException("Audio was not generate")
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
        }
    }

    private fun getHostVoice(): ScriptCharacterAudioModel {
        return ScriptCharacterAudioModel(
            voiceType = "en_US/cmu-arctic_low#jmk",
            voiceSpeedMultiplier = 1.18,
            audioSource = AudioSource.MIMIC3
        )
    }

    private fun getGuestVoice(guestCharacter: ScriptGuestCharacterModel): ScriptCharacterAudioModel {
        if (guestCharacter.characterType == CharacterType.ROBOT) {
            return ScriptCharacterAudioModel(
                voiceType = "en_US/cmu-arctic_low#fem",
                voiceSpeedMultiplier = 1.1,
                audioSource = AudioSource.MIMIC3
            )
        }

        return when (guestCharacter.speakerVoiceType) {
            SpeakerVoiceType.MALE -> maleVoices.random()
            SpeakerVoiceType.FEMALE -> femaleVoices.random()
            else -> throw IllegalArgumentException()
        }
    }
}