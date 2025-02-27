package com.charan.yourday.data.mapper

import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO


fun List<TodoistTodayTasksDTO>.toTodoData() : List<TodoData>{
    val todoList = mutableListOf<TodoData>()
    this.forEach {
        todoList.add(
            TodoData(
                id = it.id,
                tasks = it.content
            )
        )

    }
    return todoList
}