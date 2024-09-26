package com.zharguy.infinitepodcast.repos

import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import com.zharguy.infinitepodcast.repos.models.UserDataModel
import com.zharguy.infinitepodcast.repos.tables.Users
import com.zharguy.infinitepodcast.repos.tables.toUserDataModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Singleton
class UsersRepository {

    @Inject
    lateinit var db: Database

    fun createUser(user: UserDataModel): UserDataModel {
        return doRetrieveUserByExtId(extId = user.extId, extIdSource = user.userSource)
            ?: run {
                val idToReturn = Users.insertAndGetId {
                    it[extId] = user.extId
                    it[userSource] = user.userSource
                    it[createdAt] = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                }

                return retrieveUserById(idToReturn.value)
            }
    }

    fun retrieveUserById(userId: UUID): UserDataModel {
        return Users.selectAll().where { Users.id eq userId }.forUpdate()
            .map { it.toUserDataModel() }
            .single()
    }

    fun retrieveUserByExtId(extId: String, extIdSource: ExtUserSource): UserDataModel {
        return doRetrieveUserByExtId(extId = extId, extIdSource = extIdSource)
            ?: throw IllegalArgumentException("Not found for extId and source")
    }

    private fun doRetrieveUserByExtId(extId: String, extIdSource: ExtUserSource): UserDataModel? {
        return Users.selectAll().where { (Users.extId eq extId) and (Users.userSource eq extIdSource) }.forUpdate()
            .map { it.toUserDataModel() }
            .singleOrNull()
    }
}
