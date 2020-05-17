package com.paolovalerdi.abbey.ui.activities.base

import com.kabouzeid.appthemehelper.common.ATHToolbarActivity
import kotlinx.coroutines.*

abstract class AbsCoroutineActivity : ATHToolbarActivity() {

    protected val backgroundDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val job = Job()

    private val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineDispatcher = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}