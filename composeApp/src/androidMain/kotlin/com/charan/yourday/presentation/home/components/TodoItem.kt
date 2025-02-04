package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.mikepenz.markdown.compose.elements.MarkdownText
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor

@Composable
fun TodoItem(todoItem : TodoistTodayTasksDTO) {
        val richTextState = rememberRichTextState()
        richTextState.setMarkdown(todoItem.content)

        RichText(richTextState, style = MaterialTheme.typography.bodySmall,modifier = Modifier.padding(top = 20.dp))


}