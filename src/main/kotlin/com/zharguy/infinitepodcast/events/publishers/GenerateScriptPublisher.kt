package com.zharguy.infinitepodcast.events.publishers

import com.zharguy.infinitepodcast.events.EventsChannelConstants
import com.zharguy.protos.scripts.GenerateScriptEvent
import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient(EventsChannelConstants.SVC_EXCHANGE_NAME)
interface GenerateScriptPublisher {

    @Binding(EventsChannelConstants.SCRIPT_PROCESSING_QUEUE_KEY)
    fun send(event: GenerateScriptEvent)
}