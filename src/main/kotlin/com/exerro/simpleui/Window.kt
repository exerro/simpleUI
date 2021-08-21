package com.exerro.simpleui

/** An abstraction over a native window. Can pass draw functions
 *  and respond to events. */
interface Window {
    /** A stream of [WindowEvent]s that can be connected to. */
    val events: EventBus<WindowEvent>

    /** Palette of the window, used to map palette colours to
     *  RGB used for rendering. */
    var palette: Palette

    /** Returns true once the window is closed. */
    val isClosed: Boolean

    /** Submit a render function to render the window both now
     *  and whenever the window's framebuffer is refreshed
     *  (e.g. after resizing or restoring). */
    fun draw(onDraw: DrawContext.() -> Unit)

    /** Indicate that the window content should be redrawn. */
    fun redraw()

    /** Close the window. Note that no further [WindowEvent]s
     *  will be emitted on [events] and calling [draw] will have
     *  no effect. */
    fun close()
}
