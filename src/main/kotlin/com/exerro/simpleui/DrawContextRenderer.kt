package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Greyscale

/** A [DrawContextRenderer] is responsible for handling the graphical output
 *  from draw calls. It contains methods which return [DeferredDrawCall], which
 *  are collected and passed to [submit] later. */
interface DrawContextRenderer {
    /** A [DeferredDrawCall] is a graphical action yet to be taken. */
    fun interface DeferredDrawCall {
        /** Draw the respective graphics. */
        fun draw()
    }

    /** Submit a sequence of [DeferredDrawCall]s to the given [layer] and
     *  [clipRegion]. */
    fun submit(layer: Layer, clipRegion: Region, calls: Iterable<DeferredDrawCall>)

    /** Fill the region with a [colour]. */
    fun fill(
        region: Region,
        colour: Colour,
    ): DeferredDrawCall

    /** Draw a rounded rectangle, optionally providing a border. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun roundedRectangle(
        region: Region,
        cornerRadius: Pixels,
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
    ): DeferredDrawCall

    /** Draw an ellipse spanning the full area of the [region]. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun ellipse(
        region: Region,
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
    ): DeferredDrawCall

    /** Draw a shadow at the outer-edge of the current [region]. [radius]
     *  controls how far the shadow spreads. [offset] controls vertical offset
     *  downwards. [cornerRadius] gives the shadow rounded corners, and should
     *  match that of [roundedRectangle] when giving a rounded rectangle or
     *  circle a shadow. */
    fun shadow(
        region: Region,
        colour: Colour = Greyscale(0f, alpha = 0.8f),
        radius: Pixels = 10.px,
        offset: Pixels = 2f.px,
        cornerRadius: Pixels = 0.px,
    ): DeferredDrawCall

    /** Draw an image stretched to fit the [region]. If [isResource] is true,
     *  the [path] given is treated as a resource path. Otherwise, it is treated
     *  as a file name. [tint] controls tinting, where null indicates no tint
     *  (natural colour). For maintaining aspect ratio when drawing, see
     *  [Region.withAspectRatio]. */
    fun image(
        region: Region,
        path: String,
        tint: Colour? = null,
        isResource: Boolean = true,
    ): DeferredDrawCall

    /** Draw a [buffer] of text to the screen. */
    fun write(
        region: Region,
        buffer: TextBuffer<Colour>,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
    ): DeferredDrawCall
}
