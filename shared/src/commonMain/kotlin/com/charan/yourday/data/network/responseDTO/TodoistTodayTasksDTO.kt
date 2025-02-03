package com.charan.yourday.data.network.responseDTO
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class TodoistTodayTasksDTO (
    val id: String,

    @SerialName("assigner_id")
    val assignerID: JsonElement? = null,

    @SerialName("assignee_id")
    val assigneeID: JsonElement? = null,

    @SerialName("project_id")
    val projectID: String,

    @SerialName("section_id")
    val sectionID: JsonElement? = null,

    @SerialName("parent_id")
    val parentID: JsonElement? = null,

    val order: Long,
    val content: String,
    val description: String,

    @SerialName("is_completed")
    val isCompleted: Boolean,

    val labels: JsonArray,
    val priority: Long,

    @SerialName("comment_count")
    val commentCount: Long,

    @SerialName("creator_id")
    val creatorID: String,

    @SerialName("created_at")
    val createdAt: String,

    val due: Due,
    val url: String,
    val duration: JsonElement? = null,
    val deadline: JsonElement? = null
)

@Serializable
data class Due (
    val date: String,
    val string: String,
    val lang: String,

    @SerialName("is_recurring")
    val isRecurring: Boolean
)