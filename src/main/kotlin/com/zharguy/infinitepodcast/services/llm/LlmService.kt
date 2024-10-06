package com.zharguy.infinitepodcast.services.llm

import com.zharguy.infinitepodcast.repos.models.LlmModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel
import com.zharguy.infinitepodcast.services.models.ScriptGuestCharacterModel
import com.zharguy.infinitepodcast.services.models.ScriptModel

interface LlmService {
    suspend fun performInference(
        inputScript: ScriptModel,
        systemMessage: String,
        promptMessage: String,
        llmModel: LlmModel
    ): Pair<List<ScriptContentLineModel>, List<ScriptGuestCharacterModel>>
}