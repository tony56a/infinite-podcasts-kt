package com.zharguy.infinitepodcast.events.publishers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.GenerateScriptEvent
import com.zharguy.infinitepodcast.events.QueueChannelConstants
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient
import io.micronaut.rabbitmq.annotation.RabbitProperty

@RabbitClient(QueueChannelConstants.SVC_EXCHANGE_NAME)
interface GenerateScriptPublisher {

    @Binding(QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_KEY)
    fun send(event: GenerateScriptEvent, @RabbitProperty(name = "priority") priority: Int)
}