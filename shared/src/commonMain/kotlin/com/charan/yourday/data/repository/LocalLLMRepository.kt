package com.charan.yourday.data.repository

import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface LocalLLMRepository {

    fun downloadModel(modelName : String = "qwen3-0.6") : Flow<ProcessState<Boolean>>

    suspend fun isModelDownloaded(modelName: String = "qwen3-0.6") : Boolean

     suspend fun generateDaySummary(
        modelName: String = "qwen3-0.6",
        input: String
     ) : Flow<ProcessState<String>>
}