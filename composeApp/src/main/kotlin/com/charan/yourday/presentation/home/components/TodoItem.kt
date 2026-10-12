package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.ui.theme.IndexItem
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoItem(
    modifier: Modifier = Modifier,
    indexItem: IndexItem = IndexItem.FIRST_AND_LAST,
    taskName: String,
    taskLink: String,
    todoProvider: String,
    todoProviderLogo: ImageResource,
    date: String,
    isOverDue: Boolean,
    onOpenTodo: (url: String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val richTextState = rememberRichTextState()
    richTextState.setMarkdown(taskName)

    val supportingText = buildString {
        if (todoProvider.isNotEmpty()) {
            append(todoProvider)
        }
        if (date.isNotEmpty()) {
            if (isNotEmpty()) append(" • ")
            append(date)
        }
    }

    CustomListItem(
        modifier = modifier,
        indexItem = indexItem,
        contentPadding = contentPadding,
        onClick = { onOpenTodo(taskLink) },
        leadingContent = {
            Image(
                painter = painterResource(todoProviderLogo),
                contentDescription = todoProvider,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        },
        headLineContent = {
            RichText(
                state = richTextState,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
        },
        supportingContent = if (supportingText.isNotEmpty()) {
            {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else null,
        trailingContent = if (isOverDue) {
            {
                RoundedChip(
                    title = "OverDue",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(4.dp)
                )
            }
        } else null
    )
}
