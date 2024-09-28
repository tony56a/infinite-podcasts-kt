package com.zharguy.infinitepodcast.events.publishers

import build.buf.gen.com.zharguy.protos.scripts.events.v1.ShowScriptEvent
import com.google.protobuf.util.JsonFormat
import io.lettuce.core.api.StatefulRedisConnection
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class DisplayScriptPublisher {

    @Inject
    lateinit var connection: StatefulRedisConnection<String, String>

    fun publish(scriptEvent: ShowScriptEvent, queueName: String) {
        val api = connection.sync()
        api.lpush(queueName, JsonFormat.printer().print(scriptEvent))
    }
}