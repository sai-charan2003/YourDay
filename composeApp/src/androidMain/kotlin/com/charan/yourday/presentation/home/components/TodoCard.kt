package com.charan.yourday.presentation.home.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charan.yourday.MR
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.charan.yourday.home.TodoState
import com.charan.yourday.presentation.home.TodoLoadingItem
import dev.icerock.moko.resources.compose.painterResource


@Composable
fun TodoCard(
    todoState: TodoState,
    onClick : () -> Unit,
) {
    ElevatedCard(modifier = Modifier.padding(top = 20.dp).animateContentSize()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.Center) {
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
                            onClick()
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
                return@ElevatedCard
            }
            if (todoState.isLoading) {
                TodoLoadingItem()
                return@ElevatedCard
            }
            if(todoState.error!=null){
                ErrorCard()
                return@ElevatedCard
            }
            if (todoState.todoData.isNullOrEmpty().not()) {
                Text(text = "Today's tasks",style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(bottom = 20.dp))
                todoState.todoData!!.forEach {
                    TodoItem(it)
                }
                return@ElevatedCard
            }

            if(todoState.todoData.isNullOrEmpty()){
                NoTodoItem()
                return@ElevatedCard
            }

        }
    }
}


