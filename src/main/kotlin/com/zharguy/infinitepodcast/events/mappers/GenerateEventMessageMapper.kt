package com.zharguy.infinitepodcast.events.mappers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.GenerateScriptEvent
import io.micronaut.core.type.Argument
import io.micronaut.rabbitmq.bind.RabbitConsumerState
import io.micronaut.rabbitmq.intercept.MutableBasicProperties
import io.micronaut.rabbitmq.serdes.RabbitMessageSerDes
import jakarta.inject.Singleton

@Singleton
class GenerateEventMessageMapper : RabbitMessageSerDes<GenerateScriptEvent> {

    override fun serialize(data: GenerateScriptEvent, properties: MutableBasicProperties?): ByteArray =
        data.toByteArray()

    override fun deserialize(
        consumerState: RabbitConsumerState,
        argument: Argument<GenerateScriptEvent>
    ): GenerateScriptEvent = GenerateScriptEvent.parseFrom(consumerState.body)

    override fun supports(type: Argument<GenerateScriptEvent>): Boolean =
        type.type.isAssignableFrom(GenerateScriptEvent::class.java)
}

