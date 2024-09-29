package com.zharguy.infinitepodcast.events.consumers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.GenerateScriptEvent
import com.zharguy.infinitepodcast.events.QueueChannelConstants
import com.zharguy.infinitepodcast.services.ScriptService
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*

@RabbitListener
class GenerateScriptConsumer {
    companion object {
        private val logger = LoggerFactory.getLogger(ScriptService::class.java)
    }

    @Inject
    lateinit var scriptService: ScriptService

    @Queue(QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_NAME)
    fun updateAnalytics(message: GenerateScriptEvent) {
        runBlocking {
            try {
                scriptService.generateScript(UUID.fromString(message.id))
            } catch (e: Throwable) {
                logger.error("Failed to Process event", e)
            }
        }
    }

}