package com.charan.yourday.data.mapper

import com.charan.yourday.MR
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.charan.yourday.utils.DateUtils.isOverDue
import com.charan.yourday.utils.TodoProviders


fun List<TodoistTodayTasksDTO>.toTodoData() : List<TodoData>{
    val todoList = mutableListOf<TodoData>()
    this.forEach {
        todoList.add(
            TodoData(
                id = it.id,
                tasks = it.content,
                todoProvider = TodoProviders.TODOIST,
                todoProviderLogo = MR.images.Todoist,
                dueDate = it.due.date,
                dueTime = it.due.datetime,
                taskLink = it.url,
                isOverDue = it.due.date.isOverDue()


            )
        )

    }
    return todoList
}