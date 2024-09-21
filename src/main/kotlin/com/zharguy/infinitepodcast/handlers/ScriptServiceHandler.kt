package com.zharguy.infinitepodcast.handlers

import com.zharguy.infinitepodcast.handlers.mappers.toProto
import com.zharguy.infinitepodcast.handlers.mappers.toScriptModel
import com.zharguy.infinitepodcast.services.ScriptService
import com.zharguy.protos.scripts.*
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class ScriptServiceHandler : ScriptServiceGrpcKt.ScriptServiceCoroutineImplBase() {
    companion object {
        private val logger = LoggerFactory.getLogger(ScriptService::class.java)
    }
    @Inject
    lateinit var scriptService: ScriptService

    override suspend fun createScript(request: CreateScriptRequest): Script {

        return try {
            val script = request.script

            val scriptModel = scriptService.addScript(script.toScriptModel())

            scriptModel.toProto()
        } catch (e: Exception) {
            logger.error("blah", e)
            throw e
        }

    }
}