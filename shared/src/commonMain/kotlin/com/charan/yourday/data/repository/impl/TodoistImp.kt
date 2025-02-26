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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class TodoistImp(private val apiService: ApiService) : TodoistRepo {
    override suspend fun requestAuthorization() {
        openURL.openURL("https://"+todoist_base_url + "/oauth/authorize?client_id=${BuildKonfig.TODOIST_CLIENT_ID}&scope=data:read&state=secretstring")



    }

    override suspend fun getAccessToken(code: String): Flow<ProcessState<TodoistTokenDTO>> {
        val processState = MutableStateFlow<ProcessState<TodoistTokenDTO>>(ProcessState.Loading)
        try {
            val data = apiService.getTodoistAccessToken(code)
            processState.emit(ProcessState.Success(data))
        } catch (e : ClientRequestException){
            val status = e.response.status
            when (status) {
                HttpStatusCode.Unauthorized -> processState.emit(ProcessState.Error(ErrorCodes.UNAUTHORIZED.name))
                else -> processState.emit(ProcessState.Error(e.message.toString()))
            }
        }
        catch (e:Exception){
            e.printStackTrace()
            processState.emit(ProcessState.Error(e.message.toString()))
        }
        return processState



    }

    override suspend fun getTodayTasks(code : String): Flow<ProcessState<List<TodoData>>> {
        val processState = MutableStateFlow<ProcessState<List<TodoData>>>(ProcessState.Loading)
        try {

            val data = apiService.getTodoistTodayTasks(code)
            processState.emit(ProcessState.Success(data.toTodoData()))
        } catch (e:Exception){
            println(e)
            e.printStackTrace()
            processState.emit(ProcessState.Error(e.message.toString()))
        }
        return processState
    }

}