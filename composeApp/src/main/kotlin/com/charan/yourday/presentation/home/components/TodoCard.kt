package com.charan.yourday.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.Todoist
import com.charan.yourday.presentation.common.CustomListItem
import com.charan.yourday.presentation.home.TodoState
import com.charan.yourday.ui.theme.IndexItem
import com.charan.yourday.ui.theme.indexItemFor
import dev.icerock.moko.resources.compose.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoCard(
    todoState: TodoState,
    onConnect: () -> Unit,
    onTodoOpen: (link: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = "Today's Tasks",
            isLoading = todoState.isLoading,
            hasError = todoState.error
        )

        if (!todoState.isTodoAuthenticated) {
            CustomListItem(
                indexItem = IndexItem.FIRST_AND_LAST,
                leadingContent = {
                    Image(
                        painter = painterResource(MR.images.Todoist),
                        contentDescription = "Todoist",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                },
                headLineContent = {
                    Text(
                        text = "Connect to Todoist",
                        style = MaterialTheme.typography.bodyLargeEmphasized
                    )
                },
                trailingContent = {
                    FilledTonalButton(
                        onClick = onConnect,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .animateContentSize()
                            .height(32.dp),
                        enabled = !todoState.isAuthenticating,
                        shape = ButtonDefaults.filledTonalShape
                    ) {
                        if (todoState.isAuthenticating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(15.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.padding(end = 5.dp))
                        }
                        Text("Connect", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
            return
        }

        val todoList = todoState.todoData
        if (!todoList.isNullOrEmpty()) {
            Column {
                todoList.forEachIndexed { index, it ->
                    TodoItem(
                        taskName = it.taskName,
                        taskLink = it.taskLink,
                        todoProvider = it.todoProvider,
                        todoProviderLogo = it.todoImage ?: MR.images.Todoist,
                        date = it.date.orEmpty(),
                        isOverDue = it.isOverDue,
                        indexItem = todoList.indexItemFor(index),
                        onOpenTodo = onTodoOpen
                    )
                }
            }
        } else {
            NoTodoItem()
        }
    }
}
