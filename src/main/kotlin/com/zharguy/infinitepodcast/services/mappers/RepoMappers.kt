package com.zharguy.infinitepodcast.services.mappers

import com.zharguy.infinitepodcast.common.mappers.UtilMappers
import com.zharguy.infinitepodcast.repos.models.*
import com.zharguy.infinitepodcast.services.models.*
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

    fun toDataModel(promptTemplateModel: PromptTemplateModel): PromptTemplateDataModel

    fun fromDataModel(promptTemplateDataModel: PromptTemplateDataModel): PromptTemplateModel
}

private val mapper: ServiceMappers = Mappers.getMapper(ServiceMappers::class.java)

fun UserModel.toDataModel() = mapper.toDataModel(this)

fun UserDataModel.fromDataModel() = mapper.fromDataModel(this)

fun ScriptModel.toDataModel() = mapper.toDataModel(this)

fun ScriptDataModel.fromDataModel() = mapper.fromDataModel(this)

fun PromptTemplateModel.toDataModel() = mapper.toDataModel(this)

fun PromptTemplateDataModel.fromDataModel() = mapper.fromDataModel(this)