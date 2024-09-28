package com.zharguy.infinitepodcast.handlers

import build.buf.gen.com.zharguy.protos.scripts.v1.*
import com.zharguy.infinitepodcast.services.ScriptService
import com.zharguy.infinitepodcast.services.mappers.toProto
import com.zharguy.infinitepodcast.services.mappers.toScriptModel
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

    override suspend fun createScript(request: CreateScriptRequest): CreateScriptResponse {

        logger.info("Handling createScript", kv("request_id", request.script.requestId))

        return try {
            val script = request.script

            val scriptModel = scriptService.addScript(script.toScriptModel())

            createScriptResponse {
                this.script = scriptModel.toProto()
            }
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }

    override suspend fun generateScript(request: GenerateScriptRequest): GenerateScriptResponse {
        logger.info("Handling generateScript", kv("script_id", request.id))

        return try {
            val scriptModel = scriptService.generateScript(UUID.fromString(request.id))

            generateScriptResponse {
                this.script = scriptModel.toProto()
            }
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }

    override suspend fun getScript(request: GetScriptRequest): GetScriptResponse {
        return try {
            val scriptModel = scriptService.getScript(UUID.fromString(request.id))
            getScriptResponse {
                this.script = scriptModel.toProto()
            }
        } catch (e: Throwable) {
            logger.error("error", kv("exception", e))
            throw e
        }
    }
}