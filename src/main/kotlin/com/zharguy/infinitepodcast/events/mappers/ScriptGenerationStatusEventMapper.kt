package com.zharguy.infinitepodcast.events.mappers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.ScriptGenerationStatusEvent
import io.micronaut.core.type.Argument
import io.micronaut.rabbitmq.bind.RabbitConsumerState
import io.micronaut.rabbitmq.intercept.MutableBasicProperties
import io.micronaut.rabbitmq.serdes.RabbitMessageSerDes
import jakarta.inject.Singleton

@Singleton
class ScriptGenerationStatusEventMapper : RabbitMessageSerDes<ScriptGenerationStatusEvent> {

    override fun serialize(data: ScriptGenerationStatusEvent, properties: MutableBasicProperties?): ByteArray =
        data.toByteArray()

    override fun deserialize(
        consumerState: RabbitConsumerState,
        argument: Argument<ScriptGenerationStatusEvent>?
    ): ScriptGenerationStatusEvent = ScriptGenerationStatusEvent.parseFrom(consumerState.body)

    override fun supports(type: Argument<ScriptGenerationStatusEvent>?): Boolean =
        type?.type?.isAssignableFrom(ScriptGenerationStatusEvent::class.java) ?: false
}