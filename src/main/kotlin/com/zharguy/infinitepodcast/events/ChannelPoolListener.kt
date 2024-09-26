package com.zharguy.infinitepodcast.events

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.zharguy.infinitepodcast.events.EventsChannelConstants.SCRIPT_PROCESSING_QUEUE_KEY
import com.zharguy.infinitepodcast.events.EventsChannelConstants.SCRIPT_PROCESSING_QUEUE_NAME
import com.zharguy.infinitepodcast.events.EventsChannelConstants.SVC_EXCHANGE_NAME
import io.micronaut.rabbitmq.connect.ChannelInitializer
import jakarta.inject.Singleton

object EventsChannelConstants {
    const val SVC_EXCHANGE_NAME = "script_svc_exchange"
    const val SCRIPT_PROCESSING_QUEUE_NAME = "script_processing.queue"
    const val SCRIPT_PROCESSING_QUEUE_KEY = "script_processing"
}

@Singleton
class ChannelPoolListener : ChannelInitializer() {

    override fun initialize(channel: Channel, name: String) {
        channel.exchangeDeclare(SVC_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true)
        channel.queueDeclare(SCRIPT_PROCESSING_QUEUE_NAME, true, false, false, null)
        channel.queueBind(SCRIPT_PROCESSING_QUEUE_NAME, SVC_EXCHANGE_NAME, SCRIPT_PROCESSING_QUEUE_KEY)
    }
}