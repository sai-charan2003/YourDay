package com.charan.yourday.data.repository.impl

import com.cactus.CactusCompletionParams
import com.cactus.CactusLM
import com.cactus.ChatMessage
import com.cactus.InferenceMode
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalLLMRepositoryImpl(
    private val cactusLLM : CactusLM
) : LocalLLMRepository {
    override fun downloadModel(modelName: String): Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading)
        try {
            println(cactusLLM.getModels())
            cactusLLM.downloadModel(modelName)
            emit(ProcessState.Success(true))
        } catch (e: Exception) {
            emit(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun isModelDownloaded(modelName: String): Boolean {
        return cactusLLM.getModels().any{it.isDownloaded}
    }

    override suspend fun generateDaySummary(
        modelName: String,
        input: String
    ): Flow<ProcessState<String>> {
        return flow{
            println("Generating summary for input: $input")
            emit(ProcessState.Loading)
            try {
                val response = cactusLLM.generateCompletion(
                    messages = listOf(
                        ChatMessage(
                            content = input,
                            role = "user"
                        ),
                    ),
                    onToken = { token, tokenId ->
                        print(token)
                    },
                    params = CactusCompletionParams(
                        mode = InferenceMode.LOCAL_FIRST
                    )
                )
                println("Response: ${response?.response}")
                emit(ProcessState.Success(response?.response ?: ""))
            } catch (e: Exception) {
                println(e)
                emit(ProcessState.Error(e.message.toString()))
            }
        }
    }
}