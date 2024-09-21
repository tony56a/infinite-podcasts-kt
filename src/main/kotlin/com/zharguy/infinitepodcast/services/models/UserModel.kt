package com.zharguy.infinitepodcast.services.models

import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import java.time.OffsetDateTime
import java.util.*

data class UserModel(
    val id: UUID?,
    val extId: String,
    val userSource: ExtUserSource,
    val createdAt: OffsetDateTime?
)
