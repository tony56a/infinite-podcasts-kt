package com.zharguy.infinitepodcast.services.mappers

import com.zharguy.infinitepodcast.clients.models.groq.ChatCompletionModel
import com.zharguy.infinitepodcast.repos.models.LlmModel
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ValueMapping
import org.mapstruct.factory.Mappers

@Mapper
interface ClientMappers {
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.NULL)
    fun toGroqModel(llmModel: LlmModel): ChatCompletionModel
}

private val mapper: ClientMappers = Mappers.getMapper(ClientMappers::class.java)

fun LlmModel.toGroq(): ChatCompletionModel = mapper.toGroqModel(this)
