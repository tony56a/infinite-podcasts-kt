package com.zharguy.infinitepodcast.events

import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.zharguy.infinitepodcast.events.QueueChannelConstants.REQUESTER_EXCHANGE_NAME
import com.zharguy.infinitepodcast.events.QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_KEY
import com.zharguy.infinitepodcast.events.QueueChannelConstants.SCRIPT_PROCESSING_QUEUE_NAME
import com.zharguy.infinitepodcast.events.QueueChannelConstants.SCRIPT_STATUS_QUEUE_KEY
import com.zharguy.infinitepodcast.events.QueueChannelConstants.SCRIPT_STATUS_QUEUE_NAME
import com.zharguy.infinitepodcast.events.QueueChannelConstants.SVC_EXCHANGE_NAME
import io.micronaut.rabbitmq.connect.ChannelInitializer
import jakarta.inject.Singleton


@Singleton
class ChannelPoolListener : ChannelInitializer() {

    override fun initialize(channel: Channel, name: String) {
        channel.exchangeDeclare(SVC_EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true)
        channel.queueDeclare(SCRIPT_PROCESSING_QUEUE_NAME, true, false, false, null)
        channel.queueBind(SCRIPT_PROCESSING_QUEUE_NAME, SVC_EXCHANGE_NAME, SCRIPT_PROCESSING_QUEUE_KEY)

        channel.exchangeDeclare(REQUESTER_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true)
        channel.queueDeclare(
            SCRIPT_STATUS_QUEUE_NAME, true, false, false, mapOf(
                // set message TTL to 60s
                "x-message-ttl" to 60000
            )
        )
        channel.queueBind(SCRIPT_STATUS_QUEUE_NAME, REQUESTER_EXCHANGE_NAME, SCRIPT_STATUS_QUEUE_KEY)
    }
}