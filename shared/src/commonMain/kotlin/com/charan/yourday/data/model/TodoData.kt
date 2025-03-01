package com.charan.yourday.data.model

import com.charan.yourday.MR
import com.charan.yourday.utils.TodoProviders
import dev.icerock.moko.resources.ImageResource

data class TodoData(
    val id : String,
    val tasks : String?= null,
    val todoProvider : String? = null,
    val dueDate : String? = null,
    val todoProviderLogo : ImageResource? = null,
    val dueTime : String? = null
)
