package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.ScriptsRepository
import com.zharguy.infinitepodcast.repos.dbQuery
import com.zharguy.infinitepodcast.repos.models.ScriptStatus
import com.zharguy.infinitepodcast.services.mappers.fromDataModel
import com.zharguy.infinitepodcast.services.mappers.toDataModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory

@Singleton
class ScriptService {

    companion object {
        private val logger = LoggerFactory.getLogger(ScriptService::class.java)
    }

    @Inject
    lateinit var userService: UserService

    @Inject
    lateinit var scriptsRepository: ScriptsRepository

    suspend fun addScript(scriptModel: ScriptModel): ScriptModel {
        try {
            val userDataModel = dbQuery {
                // Create the user/return the existing user
                val user = userService.addUser(scriptModel.requestingUser)

                val scriptToCreate = scriptModel.copy(
                    requestingUser = user,
                    status = ScriptStatus.PENDING,
                    scriptLines = null
                )
                scriptsRepository.createScript(scriptToCreate.toDataModel())
            }
            return userDataModel.fromDataModel()
        } catch (e: Exception) {
            logger.warn("failed", StructuredArguments.kv("exception", e))
            throw e
        }

    }

}