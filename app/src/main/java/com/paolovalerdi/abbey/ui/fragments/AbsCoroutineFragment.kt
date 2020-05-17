package com.paolovalerdi.abbey.ui.fragments

import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class AbsCoroutineFragment : Fragment() {

    protected val backgroundDispatcher: CoroutineDispatcher
        get() = IO

    private val mainDispatcher: CoroutineDispatcher
        get() = Main

    private val job = Job()
    protected val scope = CoroutineScope(job + mainDispatcher)

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

    protected fun launch(
        context: CoroutineDispatcher = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)
}