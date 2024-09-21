package com.zharguy.infinitepodcast.repos.tables

import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import com.zharguy.infinitepodcast.repos.models.UserDataModel
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.kotlin.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

object Users : UUIDTable("users") {
    val extId: Column<String> = varchar("ext_id", 1000)
    val userSource: Column<ExtUserSource> = enumerationByName("user_source", 100)
    val createdAt: Column<OffsetDateTime> = timestampWithTimeZone("created_at")

    init {
        index("users_ext_id_user_source_key", isUnique = true, extId, userSource)
    }
}

fun ResultRow.toUserDataModel(): UserDataModel {
    return UserDataModel(
        id = this[Users.id].value,
        extId = this[Users.extId],
        userSource = this[Users.userSource],
        createdAt = this[Users.createdAt]
    )
}
