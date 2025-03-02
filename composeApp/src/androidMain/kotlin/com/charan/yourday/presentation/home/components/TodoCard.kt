package com.charan.yourday.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.home.TodoState
import dev.icerock.moko.resources.compose.painterResource


@Composable
fun TodoCard(
    todoState: TodoState,
    onConnect : () -> Unit,
    onTodoOpen : (link : String) -> Unit
) {

    ContentElevatedCard() {
        if (!todoState.isTodoAuthenticated) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(MR.images.Todoist),
                    null,
                    modifier = Modifier.padding(end = 10.dp).size(24.dp)
                )
                Text(text = "Connect to Todoist")
                Spacer(modifier = Modifier.weight(1f))
                FilledTonalButton(
                    onClick = {
                        onConnect()
                    },
                    contentPadding = PaddingValues(3.dp),
                    modifier = Modifier.animateContentSize().height(30.dp),
                    enabled = !todoState.isAuthenticating
                ) {
                    if (todoState.isAuthenticating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    if (todoState.isAuthenticating) {
                        Spacer(Modifier.padding(end = 5.dp))
                    }
                    Text("Connect", style = MaterialTheme.typography.labelSmall)
                }

            }
            return@ContentElevatedCard
        }
        if (todoState.isLoading) {
            TodoLoadingItem()
            return@ContentElevatedCard
        }
        if (todoState.error != null) {
            ErrorCard()
            return@ContentElevatedCard
        }
        if (todoState.todoData.isNullOrEmpty().not()) {
            Text(
                text = "Today's tasks",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(bottom = 20.dp)
            )
            todoState.todoData!!.forEach {
                TodoItem(todoItem = it, onOpenTodo = { link ->
                    onTodoOpen(link)

                })
                HorizontalDivider()
            }
            return@ContentElevatedCard
        }

        if (todoState.todoData.isNullOrEmpty()) {
            NoTodoItem()
            return@ContentElevatedCard
        }

    }
}




