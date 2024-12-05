package net.craftoriya.common

import kotlinx.coroutines.*

class TaskController {
    var shouldContinue = true
    private var _nextDelay = 0L

    val nextDelay: Long
        get() {
            val delay = _nextDelay
            _nextDelay = 0
            return delay
        }

    fun end() {
        shouldContinue = false
    }

    fun setNextDelay(millis: Long) {
        _nextDelay = millis
    }
}

fun CoroutineScope.asyncRepeat(
    initialDelay: Long,
    runnable: (TaskController) -> Unit
): Job {
    val controller = TaskController()
    return launch {
        var delayTime = initialDelay
        while (isActive && controller.shouldContinue) {
            runnable(controller)
            delayTime = controller.nextDelay.takeIf { it > 0 } ?: initialDelay
            delay(delayTime)
        }
    }
}

fun CoroutineScope.asyncDelay(
    millis: Long,
    runnable: () -> Unit
): Job = launch {
    delay(millis)
    runnable()
}
