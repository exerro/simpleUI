package com.exerro.simpleui.ui

import com.exerro.simpleui.event.EventBus
import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
class PushableEventBus<T>: EventBus<T> {
    @UndocumentedExperimentalUI
    fun push(event: T) {
        synchronized(callbacks) { callbacks.toList() }
            .forEach { it(event) }
    }

    override fun connect(onEvent: (T) -> Unit): EventBus.Connection {
        synchronized(callbacks) { callbacks.add(onEvent) }
        return EventBus.Connection {
            synchronized(callbacks) { callbacks.remove(onEvent) }
        }
    }

    private val callbacks = mutableListOf<(T) -> Unit>()
}
