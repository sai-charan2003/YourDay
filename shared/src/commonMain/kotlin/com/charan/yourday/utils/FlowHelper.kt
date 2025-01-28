package com.charan.yourday.utils
import io.ktor.utils.io.core.Closeable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)
class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    private val scope = MainScope()

    fun watch(block: (T) -> Unit): Closeable {
        val job = onEach(block).launchIn(scope)
        return object : Closeable {
            override fun close() {
                job.cancel()
            }
        }
    }
}