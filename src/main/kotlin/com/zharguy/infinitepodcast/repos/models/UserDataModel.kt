package com.zharguy.infinitepodcast.repos.models

import java.time.OffsetDateTime
import java.util.UUID

data class UserDataModel(
    val id: UUID? = null, val extId: String, val userSource: ExtUserSource, val createdAt: OffsetDateTime?
)

