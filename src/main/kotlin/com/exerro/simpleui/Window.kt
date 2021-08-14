package com.exerro.simpleui

/** An abstraction over a native window. Can pass draw functions
 *  and respond to events. */
interface Window {
    /** A stream of [WindowEvent]s that can be connected to. */
    val events: EventBus

    /** Palette of the window, used to map palette colours to
     *  RGB used for rendering. */
    var palette: Palette

    /** Returns true once the window is closed. */
    val isClosed: Boolean

    /** Submit a render function to render the window both now
     *  and whenever the window's framebuffer is refreshed
     *  (e.g. after resizing or restoring). */
    fun draw(onDraw: DrawContext.() -> Unit)

    /** Close the window. Note that no further [WindowEvent]s
     *  will be emitted on [events] and calling [draw] will have
     *  no effect. */
    fun close()

    /** A bus of events which you can [connect] to. */
    fun interface EventBus {
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
        fun connect(onEvent: (WindowEvent) -> Unit): Connection

        /** A handle to a callback registered with an [EventBus],
         *  used to disconnect that callback when it should no
         *  longer be used. */
        fun interface Connection {
            /** Disconnect the associated callback from the
             *  [EventBus] it was registered to. */
            fun disconnect()
        }
    }
}
