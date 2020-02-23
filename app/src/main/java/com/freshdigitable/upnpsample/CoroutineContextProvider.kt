package com.freshdigitable.upnpsample

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class CoroutineContextProvider(
    private val main: CoroutineDispatcher = Dispatchers.Main,
    private val io: CoroutineDispatcher = Dispatchers.IO,
    private val exceptionHandler: CoroutineExceptionHandler? = null
) {
    val mainContext: CoroutineContext
        get() = main.withHandler()

    val ioContext: CoroutineContext
        get() = io.withHandler()

    private fun CoroutineDispatcher.withHandler(): CoroutineContext {
        return this.apply {
            exceptionHandler?.let { plus(it) }
        }
    }
}
