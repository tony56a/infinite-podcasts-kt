package com.zharguy.infinitepodcast.repos

import com.zharguy.infinitepodcast.repos.models.ScriptDataModel
import com.zharguy.infinitepodcast.repos.tables.Scripts
import com.zharguy.infinitepodcast.repos.tables.Users
import com.zharguy.infinitepodcast.repos.tables.toScriptDataModel
import com.zharguy.infinitepodcast.repos.tables.toUserDataModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Singleton
class ScriptsRepository {

    @Inject
    lateinit var db: Database

    fun createScript(script: ScriptDataModel): ScriptDataModel {
        return doRetrieveScriptByRequestId(script.requestId)
            ?: run {
                val idToReturn = Scripts.insertAndGetId {
                    it[requestId] = script.requestId
                    it[requestedAt] = OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                    it[fulfilledAt] = null
                    it[requestingUser] = EntityID(requireNotNull(script.requestingUser.id), Users)
                    it[topic] = script.topic
                    it[scriptType] = script.scriptType
                    it[status] = script.status
                    it[characters] = script.characters
                    it[scriptLines] = script.scriptLines
                }

                return retrieveScriptById(idToReturn.value)
            }
    }

    fun retrieveScriptById(scriptId: UUID): ScriptDataModel {
        return (Scripts innerJoin Users).selectAll().where { (Scripts.id eq scriptId) }
            .map { val user = it.toUserDataModel()
                it.toScriptDataModel(user) }
            .single()
    }

    fun retrieveScriptByExtId(requestId: UUID): ScriptDataModel {
        return doRetrieveScriptByRequestId(requestId = requestId) ?: throw IllegalArgumentException("Not found for extId and source")
    }

    private fun doRetrieveScriptByRequestId(requestId: UUID): ScriptDataModel? {
        return (Scripts innerJoin Users).selectAll().where { (Scripts.requestId eq requestId) }
            .map { val user = it.toUserDataModel()
                it.toScriptDataModel(user) }
            .singleOrNull()
    }

}