package com.zharguy.infinitepodcast.events.consumers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.GenerateScriptEvent
import com.zharguy.infinitepodcast.events.QueueChannelConstants
import com.zharguy.infinitepodcast.services.ScriptService
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import java.util.*

private val logger = KotlinLogging.logger {}

@RabbitListener
class GenerateScriptConsumer {

    @Inject
    lateinit var scriptService: ScriptService

    @Queue(QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_NAME, prefetch = 1)
    fun processMessage(message: GenerateScriptEvent) {
        runBlocking {
            try {
                scriptService.generateScript(UUID.fromString(message.id))
            } catch (e: Throwable) {
                logger.error("Failed to Process event", e)
            }
        }
    }

}