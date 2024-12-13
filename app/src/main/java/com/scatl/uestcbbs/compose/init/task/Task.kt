package com.scatl.uestcbbs.compose.init.task

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Created by sca_tl at 2024/5/27 19:42:22
 */
abstract class Task {

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    var job: Job? = null

    open fun execute() {

    }

    open fun cancel() {
        job?.cancel()
    }
}