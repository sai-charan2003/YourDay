package com.charan.yourday.data.model

data class AIResponse(
    val modelName : String,
    val thinkingResponse : String? = null,
    val aiResponse : String? = null,
    val isThinking : Boolean = false,
)
