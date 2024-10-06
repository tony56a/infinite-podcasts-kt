package com.zharguy.infinitepodcast.events.publishers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.ScriptGenerationStatusEvent
import com.zharguy.infinitepodcast.events.QueueChannelConstants
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient(QueueChannelConstants.REQUESTER_EXCHANGE_NAME)
interface ScriptStatusPublisher {

    @Binding(QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_KEY)
    fun send(event: ScriptGenerationStatusEvent)
}