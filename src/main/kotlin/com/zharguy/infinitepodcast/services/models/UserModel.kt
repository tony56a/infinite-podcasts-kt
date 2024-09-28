package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments.kv
import java.time.OffsetDateTime
import java.util.*

data class UserModel(
    val id: UUID?,
    val extId: String,
    val userSource: ExtUserSource,
    val createdAt: OffsetDateTime?
) {
    fun getLoggerArgs(): Array<StructuredArgument> = arrayOf(
        kv("user_id", this.id),
        kv("ext_id", this.extId),
        kv("user_source", this.userSource)
    )
}
