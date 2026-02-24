package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import com.cactus.CactusCompletionParams
import com.cactus.CactusInitParams
import com.cactus.CactusLM
import com.cactus.CactusModelManager
import com.cactus.ChatMessage
import com.cactus.InferenceMode
import com.charan.yourday.data.model.AIResponse
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.data.repository.LocalLLMRepository
import com.charan.yourday.utils.ProcessState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow

class LocalLLMRepositoryImpl(
    private val cactusLLM : CactusLM,
    private val dataStoreRepo : DataStoreRepository
) : LocalLLMRepository {
    override fun downloadModel(modelName: String): Flow<ProcessState<Boolean>> = flow{
        emit(ProcessState.Loading)
        try {
            println("Downloading model: $modelName")
            cactusLLM.downloadModel(modelName)

            dataStoreRepo.setModelDownloaded(true)
            emit(ProcessState.Success(true))
        } catch (e: Exception) {
            dataStoreRepo.setModelDownloaded(false)
            emit(ProcessState.Error(e.message.toString()))
        }
    }

    override suspend fun isModelDownloaded(modelName: String): Boolean {
        return CactusModelManager.isModelDownloaded(modelName)
    }

    override suspend fun generateDaySummary(
        modelName: String,
        input: String
    ): Flow<ProcessState<AIResponse>> {
        return channelFlow{
            println("Generating summary for input: $input")
            send(ProcessState.Loading)
            try {
                cactusLLM.initializeModel(CactusInitParams(modelName))
                val thinkingResponse = StringBuilder()
                val aiResponse = StringBuilder()
                var isThinking = true
               val result =  cactusLLM.generateCompletion(
                    messages = listOf(
                        ChatMessage(
                            content = input,
                            role = "user"
                        ),
                    ),
                    onToken = { token, tokenId ->
                        val cleanToken = token
                            .replace("<think>", "")
                            .replace("</think>", "")
                        if (token.contains("<think>")) {
                            isThinking = true
                        } else if (token.contains("</think>")) {
                            isThinking = false
                        }

                        if (isThinking) {
                            thinkingResponse.append(cleanToken)
                            trySend(ProcessState.Streaming(
                                AIResponse(
                                    modelName,
                                    thinkingResponse = thinkingResponse.toString(),
                                    isThinking = isThinking
                                )
                            ))
                        } else {
                            aiResponse.append(cleanToken)
                            trySend(ProcessState.Streaming(
                                AIResponse(
                                    modelName,
                                    thinkingResponse = thinkingResponse.toString(),
                                    aiResponse = aiResponse.toString(),
                                    isThinking = isThinking
                                )
                            )
                            )
                        }
                    },
                    params = CactusCompletionParams(
                        mode = InferenceMode.LOCAL_FIRST
                    )
                )
                send(ProcessState.Success(AIResponse(
                    modelName = modelName,
                    thinkingResponse = thinkingResponse.toString(),
                    aiResponse = aiResponse.toString()
                )))
            } catch (e: Exception) {
                println(e)
                send(ProcessState.Error(e.message.toString()))
            }
        }
    }

    override suspend fun deleteModel(modelName: String): Flow<ProcessState<Boolean>> =flow{
        emit(ProcessState.Loading)
        try {
            CactusModelManager.deleteModel(modelName)
            dataStoreRepo.setModelDownloaded(true)
            emit(ProcessState.Success(true))
        } catch (e: Exception) {
            dataStoreRepo.setModelDownloaded(isModelDownloaded())
            emit(ProcessState.Error(e.message.toString()))
        }
    }
}