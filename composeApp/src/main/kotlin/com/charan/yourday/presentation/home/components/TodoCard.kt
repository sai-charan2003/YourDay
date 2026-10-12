package com.charan.yourday.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.*
import com.charan.yourday.presentation.home.TodoState
import com.charan.yourday.ui.theme.indexItemFor
import dev.icerock.moko.resources.compose.painterResource


@Composable
fun TodoCard(
    todoState: TodoState,
    onConnect : () -> Unit,
    onTodoOpen : (link : String) -> Unit
) {

    ContentElevatedCard(
        title = "Today's Tasks",
        isLoading = todoState.isLoading,
        hasError = todoState.error,
        hasContent = todoState.todoData !=null,
        content = {

            if (!todoState.isTodoAuthenticated) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(MR.images.Todoist),
                        null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(24.dp)
                    )
                    Text(text = "Connect to Todoist")
                    Spacer(modifier = Modifier.weight(1f))
                    FilledTonalButton(
                        onClick = {
                            onConnect()
                        },
                        contentPadding = PaddingValues(3.dp),
                        modifier = Modifier
                            .animateContentSize()
                            .height(30.dp),
                        enabled = !todoState.isAuthenticating,
                        shape = ButtonDefaults.filledTonalShape
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
            if (todoState.todoData.isNullOrEmpty().not()) {
                val todoList = todoState.todoData!!
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
                return@ContentElevatedCard
            }

            if (todoState.todoData?.isEmpty()==true) {
                NoTodoItem()
                return@ContentElevatedCard
            }

        }
    )
}
