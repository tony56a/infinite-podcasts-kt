package com.zharguy.infinitepodcast.handlers

import com.zharguy.infinitepodcast.handlers.mappers.toProto
import com.zharguy.infinitepodcast.handlers.mappers.toScriptModel
import com.zharguy.infinitepodcast.services.ScriptService
import com.zharguy.protos.scripts.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import java.util.*

@Singleton
class ScriptServiceHandler : ScriptServiceGrpcKt.ScriptServiceCoroutineImplBase() {
    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(ScriptService::class.java)
    }

    @Inject
    lateinit var scriptService: ScriptService

    override suspend fun createScript(request: CreateScriptRequest): Script {

        logger.info("Handling createScript", kv("request_id", request.script.requestId))

        return try {
            val script = request.script

            val scriptModel = scriptService.addScript(script.toScriptModel())

            scriptModel.toProto()
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }

    override suspend fun generateScript(request: GenerateScriptRequest): Script {
        logger.info("Handling generateScript", kv("script_id", request.id))

        return try {
            val scriptModel = scriptService.generateScript(UUID.fromString(request.id))

            scriptModel.toProto()
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }

    override suspend fun getScript(request: GetScriptRequest): Script {
        return try {
            val scriptModel = scriptService.getScript(UUID.fromString(request.id))
            scriptModel.toProto()
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }
}