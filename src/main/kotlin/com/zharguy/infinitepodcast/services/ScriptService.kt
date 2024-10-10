package com.zharguy.infinitepodcast.services

import build.buf.gen.com.zharguy.protos.scripts.enums.v1.CharacterState
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.ScriptCameraPosition
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.ScriptGenerationStatus
import build.buf.gen.com.zharguy.protos.scripts.events.v1.copy
import build.buf.gen.com.zharguy.protos.scripts.events.v1.generateScriptEvent
import build.buf.gen.com.zharguy.protos.scripts.events.v1.scriptGenerationStatusEvent
import build.buf.gen.com.zharguy.protos.scripts.events.v1.showScriptEvent
import build.buf.gen.com.zharguy.protos.scripts.models.v1.ShowScriptLine
import build.buf.gen.com.zharguy.protos.scripts.models.v1.showScriptLine
import com.google.protobuf.ByteString
import com.google.protobuf.StringValue
import com.zharguy.infinitepodcast.events.QueueChannelConstants
import com.zharguy.infinitepodcast.events.publishers.DisplayScriptPublisher
import com.zharguy.infinitepodcast.events.publishers.GenerateScriptPublisher
import com.zharguy.infinitepodcast.events.publishers.ScriptStatusPublisher
import com.zharguy.infinitepodcast.repos.ScriptsRepository
import com.zharguy.infinitepodcast.repos.dbQuery
import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.mappers.fromDataModel
import com.zharguy.infinitepodcast.services.mappers.toDataModel
import com.zharguy.infinitepodcast.services.mappers.toProto
import com.zharguy.infinitepodcast.services.models.ScriptModel
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.logstash.logback.argument.StructuredArguments.kv
import java.util.*

private val logger = KotlinLogging.logger {}

@Singleton
class ScriptService {

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var scriptGenService: ScriptGenService

    @Inject
    lateinit var audioGenerationService: AudioGenerationService

    @Inject
    lateinit var generateEventPublisher: GenerateScriptPublisher

    @Inject
    lateinit var displayScriptPublisher: DisplayScriptPublisher

    @Inject
    lateinit var scriptStatusPublisher: ScriptStatusPublisher

    @Inject
    lateinit var scriptsRepository: ScriptsRepository

    suspend fun addScript(scriptModel: ScriptModel, processSync: Boolean = false): ScriptModel {
        val persistedScript = try {
            dbQuery {
                // Create the user/return the existing user
                val user = userService.addUser(scriptModel.requestingUser)

                val scriptToCreate = scriptModel.copy(
                    requestingUser = user,
                    status = ScriptStatus.PENDING,
                    scriptLines = null
                )
                scriptsRepository.createScript(scriptToCreate.toDataModel()).fromDataModel()
            }
        } catch (e: Exception) {
            logger.warn("request failed", kv("exception", e))
            throw e
        }

        return if (processSync) {
            try {
                doGenerateScript(persistedScript)
            } catch (e: Exception) {
                logger.warn("failed to generate script for request", e)
                persistedScript
            }
        } else {
            publishScriptGenerationEvent(persistedScript)
            persistedScript
        }
    }

    suspend fun generateScriptAsync(scriptId: UUID): ScriptModel {
        val scriptModel = dbQuery {
            scriptsRepository.retrieveScriptById(scriptId).fromDataModel()
        }

        publishScriptGenerationEvent(scriptModel)

        return scriptModel
    }

    suspend fun generateScript(scriptId: UUID): ScriptModel {
        val scriptModel = dbQuery {
            scriptsRepository.retrieveScriptById(scriptId).fromDataModel()
        }
        val event = scriptGenerationStatusEvent {
            id = scriptModel.id.toString()
            scriptRequestUser = scriptModel.requestingUser.toProto()
        }

        val generatedScript = try {
            doGenerateScript(scriptModel)
        } catch (e: Throwable) {
            logger.info("publishing failure message for script generation", *scriptModel.getLoggerArgs())
            scriptStatusPublisher.send(event.copy {
                status = ScriptGenerationStatus.SCRIPT_GENERATION_STATUS_FAILED_SCRIPT
                message = StringValue.of(e.message)
            })
            throw e
        }
        val scriptWithAudio = try {
            doGenerateAudioForScript(generatedScript)
        } catch (e: Throwable) {
            logger.info("publishing failure message for audio generation", *scriptModel.getLoggerArgs())
            scriptStatusPublisher.send(event.copy {
                status = ScriptGenerationStatus.SCRIPT_GENERATION_STATUS_FAILED_AUDIO
                message = StringValue.of(e.message)
            })
            throw e
        }
        doPublishScript(scriptWithAudio)
        // Publish successful generation message to consumers
        scriptStatusPublisher.send(event.copy {
            status = ScriptGenerationStatus.SCRIPT_GENERATION_STATUS_SUCCEEDED
        })
        return generatedScript
    }


    private fun publishScriptGenerationEvent(persistedScript: ScriptModel) {
        logger.info("publishing event processing script", *persistedScript.getLoggerArgs())
        val priority = when (persistedScript.requestingUser.userSource) {
            ExtUserSource.ADMIN -> 2
            ExtUserSource.DISCORD -> 1
            ExtUserSource.TWITCH -> 1
            ExtUserSource.AUTOMATION -> 0
        }
        generateEventPublisher.send(
            event = generateScriptEvent {
                this.id = persistedScript.id.toString()
            },
            priority = priority
        )
    }

    private suspend fun doGenerateAudioForScript(script: ScriptModel): ScriptModel {
        val scriptWithAudio = audioGenerationService.generateAudioForScript(script)
        val mapping = requireNotNull(scriptWithAudio.characterVoiceMapping).mapValues { characterVoiceMapping ->
            characterVoiceMapping.value.toDataModel()
        }
        if (script.characterVoiceMapping == null) {
            dbQuery {
                scriptsRepository.updateScriptAudio(requireNotNull(scriptWithAudio.id), mapping)
            }
        }

        return scriptWithAudio
    }

    private fun doPublishScript(script: ScriptModel) {
        logger.info("Publishing script", *script.getLoggerArgs())
        val lines: List<ShowScriptLine> = requireNotNull(script.scriptLines).mapIndexed { index, line ->
            val cleanedSpeakerName = line.speaker.lowercase()
            val cameraPosition = when {
                index >= script.scriptLines.size - 1 -> ScriptCameraPosition.SCRIPT_CAMERA_POSITION_SCENE_OVERVIEW
                cleanedSpeakerName.contains("poe") || cleanedSpeakerName.contains("host") ->
                    ScriptCameraPosition.SCRIPT_CAMERA_POSITION_HOST

                else -> ScriptCameraPosition.SCRIPT_CAMERA_POSITION_GUEST
            }
            val (hostState, guestState) = when {
                cleanedSpeakerName.contains("poe") || cleanedSpeakerName.contains("host") ->
                    Pair(CharacterState.CHARACTER_STATE_SPEAKING, CharacterState.CHARACTER_STATE_NEUTRAL)

                else ->
                    Pair(CharacterState.CHARACTER_STATE_NEUTRAL, CharacterState.CHARACTER_STATE_SPEAKING)
            }
            showScriptLine {
                this.position = cameraPosition
                this.speaker = line.speaker
                this.content = line.content
                this.hostState = hostState
                this.guestState = guestState
                this.audioContent = ByteString.copyFrom(requireNotNull(script.scriptLineAudio).getValue(line))
            }
        }

        val event = showScriptEvent {
            this.topic = script.topic
            this.guestCharacter = requireNotNull(script.characters).first().toProto()
            this.scriptType = script.scriptType.toProto()
            this.requestingUser = script.requestingUser.toProto()
            this.lines.addAll(lines)
        }
        val displayScriptQueue = when (script.requestingUser.userSource) {
            ExtUserSource.AUTOMATION -> QueueChannelConstants.SCRIPT_DISPLAY_QUEUE_NAME
            else -> QueueChannelConstants.PRIORITY_SCRIPT_DISPLAY_QUEUE_NAME
        }
        displayScriptPublisher.publish(event, displayScriptQueue)
    }

    private suspend fun doGenerateScript(script: ScriptModel): ScriptModel {
        if (script.status == ScriptStatus.GENERATED) {
            logger.warn("script is already generated, returning", *script.getLoggerArgs())
            return script
        }
        val updatedScript = scriptGenService.performInference(script)
        val retVal = dbQuery {
            scriptsRepository.updateScriptContent(updatedScript.toDataModel()).fromDataModel()
        }
        return retVal
    }

    suspend fun getScript(scriptId: UUID): ScriptModel {
        return dbQuery {
            scriptsRepository.retrieveScriptById(scriptId).fromDataModel()
        }
    }

}