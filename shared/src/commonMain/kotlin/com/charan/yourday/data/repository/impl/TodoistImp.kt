package com.charan.yourday.data.repository.impl

import com.charan.yourday.BuildKonfig
import com.charan.yourday.data.mapper.toTodoData
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.network.Ktor.ApiService
import com.charan.yourday.data.network.Ktor.todoist_base_url
import com.charan.yourday.data.network.responseDTO.TodoistTodayTasksDTO
import com.charan.yourday.data.network.responseDTO.TodoistTokenDTO
import com.charan.yourday.data.network.responseDTO.WeatherDTO
import com.charan.yourday.data.repository.TodoistRepo
import com.charan.yourday.utils.ErrorCodes
import com.charan.yourday.utils.ProcessState
import com.charan.yourday.utils.openURL
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow


class TodoistImp(private val apiService: ApiService) : TodoistRepo {
    override suspend fun requestAuthorization() {
        openURL.openURL("https://"+todoist_base_url + "/oauth/authorize?client_id=${BuildKonfig.TODOIST_CLIENT_ID}&scope=data:read&state=secretstring")

    }

    override suspend fun getAccessToken(code: String): Flow<ProcessState<TodoistTokenDTO>> = flow{
        emit(ProcessState.Loading)
        try {
            val response = apiService.getTodoistAccessToken(code)
            when(response.status){
                HttpStatusCode.OK -> {
                    emit(ProcessState.Success(response.body<TodoistTokenDTO>()))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error("API Error: ${response.status}"))
                }

            }
        } catch (e :Exception){
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }
    }

    override suspend fun getTodayTasks(code : String): Flow<ProcessState<List<TodoData>>> =flow{
        emit(ProcessState.Loading)
        try {
            val response = apiService.getTodoistTodayTasks(code)
            when(response.status){
                HttpStatusCode.OK -> {
                    emit(ProcessState.Success(response.body<List<TodoistTodayTasksDTO>>().toTodoData()))
                }
                HttpStatusCode.Unauthorized -> {
                    emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                }
                else -> {
                    emit(ProcessState.Error("API Error: ${response.status}"))
                }

            }
        } catch (e :Exception){
            emit(ProcessState.Error(e.message ?: "Unknown Error"))
        }

    }

}