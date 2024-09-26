package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.events.publishers.GenerateScriptPublisher
import com.zharguy.infinitepodcast.repos.ScriptsRepository
import com.zharguy.infinitepodcast.repos.dbQuery
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.mappers.fromDataModel
import com.zharguy.infinitepodcast.services.mappers.toDataModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import com.zharguy.protos.scripts.GenerateScriptEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import java.util.*

@Singleton
class ScriptService {

    companion object {
        private val logger = LoggerFactory.getLogger(ScriptService::class.java)
    }

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var scriptGenService: ScriptGenService

    @Inject
    lateinit var audioGenerationService: AudioGenerationService

    @Inject
    lateinit var generateEventPublisher: GenerateScriptPublisher

    @Inject
    lateinit var scriptsRepository: ScriptsRepository

    suspend fun addScript(scriptModel: ScriptModel, processSync: Boolean = false): ScriptModel {
        val persistedScript = try {
            val scriptDataModel = dbQuery {
                // Create the user/return the existing user
                val user = userService.addUser(scriptModel.requestingUser)

                val scriptToCreate = scriptModel.copy(
                    requestingUser = user,
                    status = ScriptStatus.PENDING,
                    scriptLines = null
                )
                scriptsRepository.createScript(scriptToCreate.toDataModel())
            }
            scriptDataModel.fromDataModel()

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
            logger.info("publishing event processing script", kv("script_id", persistedScript.id))
            generateEventPublisher.send(
                GenerateScriptEvent.newBuilder().setId(persistedScript.id.toString()).build()
            )
            persistedScript
        }

    }

    suspend fun generateScript(scriptId: UUID): ScriptModel {
        val scriptModel = dbQuery {
            scriptsRepository.retrieveScriptById(scriptId).fromDataModel()
        }
        return doGenerateScript(scriptModel)
    }

    private suspend fun doGenerateScript(script: ScriptModel): ScriptModel {
        if (script.status == ScriptStatus.GENERATED) {
            logger.warn("script is already generated, returning", kv("script_id", script.id))
            return script
        }
        val updatedScript = scriptGenService.performInference(script)
        val retVal = dbQuery {
            scriptsRepository.updateScriptContent(updatedScript.toDataModel()).fromDataModel()
        }

        val audioMap = audioGenerationService.generateAudioForScript(updatedScript)
        return retVal
    }

}