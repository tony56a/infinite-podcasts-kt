package com.zharguy.infinitepodcast.handlers

import build.buf.gen.com.zharguy.protos.scripts.v1.*
import com.zharguy.infinitepodcast.services.RateLimiterService
import com.zharguy.infinitepodcast.services.ScriptService
import com.zharguy.infinitepodcast.services.mappers.toProto
import com.zharguy.infinitepodcast.services.mappers.toScriptModel
import com.zharguy.infinitepodcast.services.mappers.toUserModel
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.grpc.annotation.GrpcService
import jakarta.inject.Inject
import net.logstash.logback.argument.StructuredArguments.kv
import java.util.*

const val CREATE_SCRIPT_METHOD_NAME = "createScript"
const val GENERATE_SCRIPT_METHOD_NAME = "generateScript"

private val logger = KotlinLogging.logger {}

@GrpcService
class ScriptServiceHandler : ScriptServiceGrpcKt.ScriptServiceCoroutineImplBase() {

    @Inject
    lateinit var rateLimiterService: RateLimiterService

    @Inject
    lateinit var scriptService: ScriptService

    override suspend fun createScript(request: CreateScriptRequest): CreateScriptResponse {
        logger.info("Handling createScript", kv("request_id", request.script.requestId))

        val requestingUser = request.script.requestingUser.toUserModel()
        if (!rateLimiterService.checkLimit(
                requestingUser.extId,
                requestingUser.userSource,
                CREATE_SCRIPT_METHOD_NAME
            )
        ) {
            throw HandlerExceptions.RateLimitedException(CREATE_SCRIPT_METHOD_NAME)
        }


        val script = request.script
        val scriptModel = scriptService.addScript(script.toScriptModel())

        return createScriptResponse {
            this.script = scriptModel.toProto()
        }
    }

    override suspend fun generateScript(request: GenerateScriptRequest): GenerateScriptResponse {
        logger.info("Handling generateScript", kv("script_id", request.id))

        val requestingUser = request.requestingUser.toUserModel()
        if (!rateLimiterService.checkLimit(
                requestingUser.extId,
                requestingUser.userSource,
                GENERATE_SCRIPT_METHOD_NAME
            )
        ) {
            throw HandlerExceptions.RateLimitedException(GENERATE_SCRIPT_METHOD_NAME)
        }

        val scriptModel =
            scriptService.generateScriptAsync(UUID.fromString(request.id))

        return generateScriptResponse {
            this.script = scriptModel.toProto()
        }
    }

    override suspend fun getScript(request: GetScriptRequest): GetScriptResponse {
        logger.info("Handling getScript", kv("script_id", request.id))

        val scriptModel = scriptService.getScript(UUID.fromString(request.id))
        return getScriptResponse {
            this.script = scriptModel.toProto()
        }
    }
}