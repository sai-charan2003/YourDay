package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.charan.yourday.utils.DateUtils.convertToMMMDYYYY
import com.charan.yourday.utils.DateUtils.convertToMMMDYYYYWithTime
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import dev.icerock.moko.resources.compose.painterResource
import org.jetbrains.compose.resources.imageResource

@Composable
fun TodoItem(todoItem: TodoData) {
        val richTextState = rememberRichTextState()
        richTextState.setMarkdown(todoItem.tasks ?: "")

        Column(
                modifier = Modifier
                        .fillMaxWidth()
        ) {
                RichText(
                        state = richTextState,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                        painter = painterResource(
                                                todoItem.todoProviderLogo ?: MR.images.error
                                        ),
                                        contentDescription = "Todo Provider",
                                        contentScale = ContentScale.FillBounds,
                                        modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape),


                                        )



                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                        text = todoItem.todoProvider ?: "Unknown Provider",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }


                                Text(
                                        text = todoItem.dueTime?.convertToMMMDYYYYWithTime() ?: todoItem.dueDate?.convertToMMMDYYYY() ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        }
}


@Composable
@Preview
fun TodoItemPreview(){
        TodoItem(
                TodoData(
                id = "1",
                tasks = "test",
                        todoProviderLogo = MR.images.Todoist
                )


        )
}