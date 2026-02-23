package com.charan.yourday.data.repository

import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow

interface LocalLLMRepository {

    fun downloadModel(modelName : String = "qwen3-1.7-pro") : Flow<ProcessState<Boolean>>

    suspend fun isModelDownloaded(modelName: String = "qwen3-1.7-pro") : Boolean

     suspend fun generateDaySummary(
        modelName: String = "qwen3-1.7-pro",
        input: String
     ) : Flow<ProcessState<AIResponse>>

     suspend fun deleteModel(modelName: String = "qwen3-1.7-pro") : Flow<ProcessState<Boolean>>
}