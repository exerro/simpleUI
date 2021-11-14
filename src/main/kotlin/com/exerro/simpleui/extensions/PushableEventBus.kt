package com.exerro.simpleui.extensions

import com.exerro.simpleui.EventBus
import com.exerro.simpleui.Undocumented

@Undocumented
class PushableEventBus<T>: EventBus<T> {
    @Undocumented
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
