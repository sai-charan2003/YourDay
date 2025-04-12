package com.charan.yourday.presentation.home.components

import android.util.Log
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
import com.charan.yourday.data.model.todoProvider
import com.charan.yourday.home.TodoState
import dev.icerock.moko.resources.compose.painterResource


@Composable
fun TodoCard(
    todoState: TodoState,
    onConnect : (todoProvider : String) -> Unit,
    onTodoOpen : (link : String) -> Unit
) {

    ContentElevatedCard(
        title = "Today's Tasks",
        isLoading = todoState.isLoading,
        hasError = todoState.todoProviderState.any { it.value.error.isNullOrEmpty().not() },
        hasContent = todoState.todoData !=null,
        content = {

            if (!todoState.isAnyTodoAuthenticated) {
                todoProvider.forEach {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(it.providerLogo),
                            null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(24.dp)
                        )
                        Text(text = "Connect to ${it.providerName}")
                        Spacer(modifier = Modifier.weight(1f))
                        FilledTonalButton(
                            onClick = {
                                onConnect(it.providerName)
                            },
                            contentPadding = PaddingValues(3.dp),
                            modifier = Modifier
                                .animateContentSize()
                                .height(30.dp),
                            enabled = todoState.todoProviderState[it.providerName]?.isAuthenticating == false
                        ) {
                            if (todoState.todoProviderState[it.providerName]?.isAuthenticating == true) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(15.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            if (todoState.todoProviderState[it.providerName]?.isAuthenticating == true) {
                                Spacer(Modifier.padding(end = 5.dp))
                            }
                            Text("Connect", style = MaterialTheme.typography.labelSmall)
                        }

                    }

                }

                return@ContentElevatedCard
            }
            if (todoState.todoData.isNullOrEmpty().not()) {
                todoState.todoData!!.forEach {
                    TodoItem(todoItem = it, onOpenTodo = { link ->
                        onTodoOpen(link)
                    })
                    HorizontalDivider()
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




