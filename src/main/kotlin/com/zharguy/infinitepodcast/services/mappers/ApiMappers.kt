package com.zharguy.infinitepodcast.services.mappers

import com.zharguy.infinitepodcast.common.mappers.UtilMappers
import com.zharguy.infinitepodcast.repos.models.*
import com.zharguy.infinitepodcast.services.models.*
import org.mapstruct.*
import org.mapstruct.factory.Mappers
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.CharacterType as CharacterTypeProto
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.ExtUserSource as ExtUserSourceProto
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.LLMModel as LLMModelProto
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.ScriptType as ScriptTypeProto
import build.buf.gen.com.zharguy.protos.scripts.enums.v1.SpeakerVoiceType as SpeakerVoiceTypeProto
import build.buf.gen.com.zharguy.protos.scripts.models.v1.PromptTemplate as PromptTemplateProto
import build.buf.gen.com.zharguy.protos.scripts.models.v1.Script as ScriptProto
import build.buf.gen.com.zharguy.protos.scripts.models.v1.ScriptContentLine as ScriptContentLineProto
import build.buf.gen.com.zharguy.protos.scripts.models.v1.ScriptGuestCharacter as ScriptGuestCharacterProto
import build.buf.gen.com.zharguy.protos.scripts.models.v1.User as UserProto

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
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    abstract fun toModel(userSourceProto: CharacterTypeProto): CharacterType?

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: CharacterType): CharacterTypeProto

    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "LLM_MODEL_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    abstract fun toModel(llmModel: LLMModelProto): LlmModel

    @InheritInverseConfiguration
    abstract fun toProto(llmModel: LlmModel): LLMModelProto

    @EnumMapping(
        nameTransformationStrategy = MappingConstants.STRIP_PREFIX_TRANSFORMATION,
        configuration = "SPEAKER_VOICE_TYPE_"
    )
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    abstract fun toModel(userSourceProto: SpeakerVoiceTypeProto): SpeakerVoiceType?

    @InheritInverseConfiguration
    abstract fun toProto(userSourceProto: SpeakerVoiceType): SpeakerVoiceTypeProto

    abstract fun toModel(proto: UserProto): UserModel
    abstract fun toProto(model: UserModel): UserProto

    abstract fun toModel(proto: ScriptGuestCharacterProto): ScriptGuestCharacterModel
    abstract fun toProto(model: ScriptGuestCharacterModel): ScriptGuestCharacterProto

    abstract fun toModel(proto: ScriptContentLineProto): ScriptContentLineModel
    abstract fun toProto(model: ScriptContentLineModel): ScriptContentLineProto

    abstract fun toModel(proto: ScriptProto): ScriptModel
    abstract fun toProto(model: ScriptModel): ScriptProto

    @Mapping(source = "promptTemplate", target = "promptTemplateText")
    abstract fun toModel(proto: PromptTemplateProto): PromptTemplateModel

    @InheritInverseConfiguration
    abstract fun toProto(model: PromptTemplateModel): PromptTemplateProto


    @AfterMapping
    fun mapCharacterLinesToProto(model: ScriptModel, @MappingTarget scriptProto: ScriptProto.Builder) {
        scriptProto.addAllCharacters(model.characters?.map { character ->
            mapper.toProto(character)
        } ?: emptyList())

        scriptProto.addAllScriptLines(model.scriptLines?.map { line ->
            mapper.toProto(line)
        } ?: emptyList())
    }

    @AfterMapping
    fun mapCharacterFromProto(scriptProto: ScriptProto, @MappingTarget model: ScriptModel): ScriptModel {
        return model.copy(
            characters = scriptProto.charactersList.map { character -> mapper.toModel(character) }
        )
    }
}

private val mapper: ApiMappers = Mappers.getMapper(ApiMappers::class.java)

fun ScriptType.toProto(): ScriptTypeProto = mapper.toProto(this)

fun UserProto.toUserModel(): UserModel = mapper.toModel(this)

fun UserModel.toProto(): UserProto = mapper.toProto(this)

fun ScriptGuestCharacterModel.toProto(): ScriptGuestCharacterProto = mapper.toProto(this)

fun ScriptProto.toScriptModel(): ScriptModel = mapper.toModel(this)

fun ScriptModel.toProto(): ScriptProto = mapper.toProto(this)

fun PromptTemplateProto.toPromptTemplateModel(): PromptTemplateModel = mapper.toModel(this)

fun PromptTemplateModel.toProto(): PromptTemplateProto = mapper.toProto(this)