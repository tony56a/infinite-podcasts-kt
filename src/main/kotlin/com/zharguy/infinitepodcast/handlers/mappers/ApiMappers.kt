package com.zharguy.infinitepodcast.handlers.mappers

import com.zharguy.infinitepodcast.common.mappers.UtilMappers
import com.zharguy.infinitepodcast.repos.models.CharacterType
import com.zharguy.infinitepodcast.repos.models.ExtUserSource
import com.zharguy.infinitepodcast.repos.models.ScriptType
import com.zharguy.infinitepodcast.repos.models.SpeakerVoiceType
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel
import com.zharguy.infinitepodcast.services.models.UserModel
import com.zharguy.protos.common.User as UserProto
import com.zharguy.protos.scripts.ScriptContentLine
import com.zharguy.protos.scripts.ScriptGuestCharacter
import org.mapstruct.*
import org.mapstruct.factory.Mappers
import com.zharguy.protos.common.ExtUserSource as ExtUserSourceProto
import com.zharguy.protos.scripts.CharacterType as CharacterTypeProto
import com.zharguy.protos.scripts.Script as ScriptProto
import com.zharguy.protos.scripts.ScriptType as ScriptTypeProto
import com.zharguy.protos.scripts.SpeakerVoiceType as SpeakerVoiceTypeProto

@Mapper(
    uses = [UtilMappers::class],
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
abstract class ApiMappers {

    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "EXT_USER_SOURCE_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    abstract fun toModel(userSourceProto: ExtUserSourceProto): ExtUserSource

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: ExtUserSource): ExtUserSourceProto

    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "SCRIPT_TYPE_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    abstract fun toModel(userSourceProto: ScriptTypeProto): ScriptType

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: ScriptType): ScriptTypeProto

    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "CHARACTER_TYPE_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    abstract fun toModel(userSourceProto: CharacterTypeProto): CharacterType

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: CharacterType): CharacterTypeProto


    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "SPEAKER_VOICE_TYPE_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    abstract fun toModel(userSourceProto: SpeakerVoiceTypeProto): SpeakerVoiceType

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: SpeakerVoiceType): SpeakerVoiceTypeProto

    abstract fun toModel(userProto: UserProto): UserModel
    abstract fun toProto(userProto: UserModel): UserProto

    abstract fun toModel(userProto: ScriptGuestCharacter): ScriptGuestCharacterModel
    abstract fun toProto(userProto: ScriptGuestCharacterModel): ScriptGuestCharacter

    abstract fun toModel(userProto: ScriptContentLine): ScriptContentLineModel
    abstract fun toProto(userProto: ScriptContentLineModel): ScriptContentLine

    abstract fun toModel(userProto: ScriptProto): ScriptModel
    @Mappings(
        Mapping(target = "characters", ignore = true)
    )
    abstract fun toProto(userProto: ScriptModel): ScriptProto

    @AfterMapping
    fun mapCharacterLinesToProto(model: ScriptModel, @MappingTarget scriptProto: ScriptProto.Builder) {
        scriptProto.putAllCharacters(model.characters?.map {
            (name, character) -> name to mapper.toProto(character)
        }?.toMap() ?: emptyMap())

        scriptProto.addAllScriptLines(model.scriptLines?.map {
            line -> mapper.toProto(line)
        } ?: emptyList())
    }
}

private val mapper: ApiMappers = Mappers.getMapper(ApiMappers::class.java)

fun UserProto.toUserModel(): UserModel = mapper.toModel(this)

fun UserModel.toProto(): UserProto = mapper.toProto(this)

fun ScriptProto.toScriptModel(): ScriptModel = mapper.toModel(this)

fun ScriptModel.toProto(): ScriptProto = mapper.toProto(this)