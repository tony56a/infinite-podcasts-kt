package com.zharguy.infinitepodcast.services

import com.zharguy.infinitepodcast.repos.UsersRepository
import com.zharguy.infinitepodcast.repos.dbQuery
import com.zharguy.infinitepodcast.services.mappers.fromDataModel
import com.zharguy.infinitepodcast.services.mappers.toDataModel
import com.zharguy.infinitepodcast.services.models.UserModel
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*

@Singleton
class UserService {

    @Inject
    lateinit var usersRepository: UsersRepository

    suspend fun addUser(userModel: UserModel): UserModel {
        val userDataModel = dbQuery {
            usersRepository.createUser(userModel.toDataModel())
        }
        return userDataModel.fromDataModel()
    }

    suspend fun retrieveUser(userId: UUID): UserModel {
        val userDataModel = dbQuery {
            usersRepository.retrieveUserById(userId)
        }
        return userDataModel.fromDataModel()
    }
}