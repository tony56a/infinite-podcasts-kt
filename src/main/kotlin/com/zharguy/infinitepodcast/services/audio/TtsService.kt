package com.zharguy.infinitepodcast.services.audio

import com.zharguy.infinitepodcast.services.models.ScriptCharacterAudioModel
import com.zharguy.infinitepodcast.services.models.ScriptContentLineModel

interface TtsService {
    suspend fun generateAudio(
        scriptLines: List<ScriptContentLineModel>,
        characterVoiceMap: Map<String, ScriptCharacterAudioModel>
    ): Map<ScriptContentLineModel, ByteArray>
}