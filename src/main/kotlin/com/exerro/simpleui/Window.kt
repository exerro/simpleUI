package com.exerro.simpleui

import com.exerro.simpleui.event.EventBus
import com.exerro.simpleui.event.WindowEvent
import com.exerro.simpleui.experimental.Palette
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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

    /** A [TimeSource] implementation used to get [TimeMark]s. */
    val timeSource: TimeSource

    /** [TimeMark] representing when the window was created. */
    val createdAt: TimeMark

    /** Current width of the window, in pixels. */
    val currentWidth: Int

    /** Current height of the window, in pixels. */
    val currentHeight: Int

    /** Submit a render function to render the window both now
     *  and whenever the window's framebuffer is refreshed
     *  (e.g. after resizing or restoring). */
    fun draw(
        layers: LayerComposition = LayerComposition.Default,
        onDraw: DrawContext.(deltaTime: Duration) -> Unit,
    )

    /** Indicate that the window content should be redrawn. */
    fun redraw()

    /** Close the window. Note that no further [WindowEvent]s
     *  will be emitted on [events] and calling [draw] will have
     *  no effect. */
    fun close()
}
