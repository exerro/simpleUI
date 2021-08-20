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

    /** Map events (optionally to a new type) using [fn]. */
    fun <T> map(fn: (E) -> T) = EventBus<T> { onEvent ->
        connect { e -> onEvent(fn(e)) }
    }

    /** Filter events using [predicate] such that anything connected to the
     *  [EventBus] returned will match [predicate]. */
    fun filter(predicate: (E) -> Boolean) = EventBus<E> { onEvent ->
        connect { e -> if (predicate(e)) onEvent(e) }
    }

    /** A handle to a callback registered with an [EventBus],
     *  used to disconnect that callback when it should no
     *  longer be used. */
    fun interface Connection {
        /** Disconnect the associated callback from the
         *  [EventBus] it was registered to. */
        fun disconnect()

        companion object {
            /** Return a [Connection] that, when disconnected from, disconnects
             *  from all of [connections]. */
            fun join(connections: Iterable<Connection>) = Connection {
                connections.forEach(Connection::disconnect)
            }

            /** Return a [Connection] that, when disconnected from, disconnects
             *  from all of [connections]. */
            fun join(vararg connections: Connection) = Connection {
                connections.forEach(Connection::disconnect)
            }
        }
    }
}

/** Filter events to type [T]. */
inline fun <reified T> EventBus<*>.filterIsInstance() = EventBus<T> { onEvent ->
    connect { e -> if (e is T) onEvent(e) }
}
