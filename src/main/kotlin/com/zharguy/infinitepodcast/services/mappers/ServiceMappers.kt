package com.zharguy.infinitepodcast.services.mappers

import com.zharguy.infinitepodcast.common.mappers.UtilMappers
import com.zharguy.infinitepodcast.repos.models.ScriptContentLineDataModel
import com.zharguy.infinitepodcast.repos.models.ScriptDataModel
import com.zharguy.infinitepodcast.repos.models.ScriptGuestCharacterDataModel
import com.zharguy.infinitepodcast.repos.models.UserDataModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import com.zharguy.infinitepodcast.services.models.UserModel
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(uses = [UtilMappers::class])
interface ServiceMappers {

    fun toDataModel(userModel: UserModel): UserDataModel

    fun fromDataModel(userModel: UserDataModel): UserModel

    fun toDataModel(scriptModel: ScriptModel): ScriptDataModel

    fun fromDataModel(userModel: ScriptDataModel): ScriptModel

    fun toDataModel(scriptModel: ScriptContentLineModel): ScriptContentLineDataModel

    fun fromDataModel(userModel: ScriptContentLineDataModel): ScriptContentLineModel

    fun toDataModel(scriptModel: ScriptGuestCharacterModel): ScriptGuestCharacterDataModel

    fun fromDataModel(userModel: ScriptGuestCharacterDataModel): ScriptGuestCharacterModel
}

private val mapper: ServiceMappers = Mappers.getMapper(ServiceMappers::class.java)

fun UserModel.toDataModel() = mapper.toDataModel(this)

fun UserDataModel.fromDataModel() = mapper.fromDataModel(this)

fun ScriptModel.toDataModel() = mapper.toDataModel(this)

fun ScriptDataModel.fromDataModel() = mapper.fromDataModel(this)