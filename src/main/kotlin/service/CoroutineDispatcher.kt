package main.kotlin.service

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

sealed interface CoroutineDispatcher<T> {

    fun launchCoroutine(
        context: CoroutineContext,
        param: T,
        func: (T) -> Unit,
    ) = runBlocking { withContext(context) { func.invoke(param) } }
}
