package com.exerro.simpleui

/** A bus of events which you can [connect] to. */
fun interface EventBus<out E> {
    /** Connect a callback [onEvent] to the event bus.
     *  Whenever an event is emitted on this bus, [onEvent]
     *  will be called with that event as its parameter. A
     *  [Connection] object is returned which can be used to
     *  [disconnect][Connection.disconnect] the callback
     *  when events no longer need to be handled.
     *
     *  Note that any long-running processing should be
     *  run on another thread since blocking in response to
     *  an event here will prevent further events from being
     *  handled. */
    fun connect(onEvent: (E) -> Unit): Connection

    @Undocumented
    fun <T> map(fn: (E) -> T) = EventBus<T> { onEvent ->
        connect { e -> onEvent(fn(e)) }
    }

    @Undocumented
    fun filter(fn: (E) -> Boolean) = EventBus<E> { onEvent ->
        connect { e -> if (fn(e)) onEvent(e) }
    }

    /** A handle to a callback registered with an [EventBus],
     *  used to disconnect that callback when it should no
     *  longer be used. */
    fun interface Connection {
        /** Disconnect the associated callback from the
         *  [EventBus] it was registered to. */
        fun disconnect()
    }
}

@Undocumented
inline fun <reified T> EventBus<*>.filterIsInstance() = EventBus<T> { onEvent ->
    connect { e -> if (e is T) onEvent(e) }
}
