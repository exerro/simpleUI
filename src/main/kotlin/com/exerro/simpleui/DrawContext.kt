package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Greyscale
import com.exerro.simpleui.internal.DrawContextImpl

/** A [DrawContext] provides drawing capabilities. The context is associated
 *  with a [region], representing the area on-screen where content is drawn.
 *  Various methods draw content within this [region], such as [fill], and
 *  [roundedRectangle].
 *  Initially, [region] represents the window content area. To draw in a
 *  different [Region], the [region] field can be used to derive a sub-region
 *  then use [draw] to draw within that instead. */
interface DrawContext {
    /** Area on-screen where content is drawn. */
    val region: Region

    /** Area on-screen where content is visible. */
    val clipRegion: Region

    /** Fill the region with a [colour]. */
    fun fill(
        colour: Colour,
    )

    /** Draw a rounded rectangle, optionally providing a border. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun roundedRectangle(
        cornerRadius: Pixels,
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
    )

    /** Draw an ellipse spanning the full area of the [region]. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun ellipse(
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
    )

    /** Draw a shadow at the outer-edge of the current [region]. [radius]
     *  controls how far the shadow spreads. [offset] controls vertical offset
     *  downwards. [cornerRadius] gives the shadow rounded corners, and should
     *  match that of [roundedRectangle] when giving a rounded rectangle or
     *  circle a shadow. */
    fun shadow(
        colour: Colour = Greyscale(0f, alpha = 0.8f),
        radius: Pixels = 10.px,
        offset: Pixels = 2f.px,
        cornerRadius: Pixels = 0.px,
    )

    /** Draw an image stretched to fit the [region]. If [isResource] is true,
     *  the [path] given is treated as a resource path. Otherwise, it is treated
     *  as a file name. [tint] controls tinting, where null indicates no tint
     *  (natural colour). For maintaining aspect ratio when drawing, see
     *  [Region.withAspectRatio]. */
    fun image(
        path: String,
        tint: Colour? = null,
        isResource: Boolean = true,
    )

    /** Write complex formatted text, using [writer]. */
    fun write(
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
        initialIndentation: Int = 0,
        wrap: Boolean = true,
        writer: TextDrawContext.() -> Unit,
    )

    /** Write a simple string of text in the specified colour. */
    fun write(
        text: String,
        colour: Colour,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        wrap: Boolean = true,
        underlineColour: Colour? = null,
        highlightColour: Colour? = null,
        strikeThroughColour: Colour? = null,
    ) = write(
        font = font,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
        wrap = wrap
    ) {
        underlineColour?.let { beginUnderlining(it) }
        highlightColour?.let { beginHighlighting(it) }
        strikeThroughColour?.let { beginStrikingThrough(it) }
        text(text, colour, splitAtSpaces = wrap)
    }

//    @Undocumented
//    fun <T> animated(
//        from: T, to: T,
//        interpolate: (t: Float, from: T, to: T) -> T,
//        duration: Duration = Duration.seconds(0.25),
//        draw: DrawContext.(value: T) -> Unit,
//    )

    /** Draw content within another region. When [clip] is true, all content
     *  drawn within the callback [draw] is clipped to the region given. */
    fun Region.draw(
        clip: Boolean = false,
        draw: DrawContext.() -> Unit,
    )

    /** Shorthand for drawing a list of regions. See [DrawContext.draw]. [draw]
     *  callback receives an additional parameter for the index of the region
     *  being drawn. */
    fun List<Region>.draw(
        clip: Boolean = false,
        draw: DrawContext.(index: Int) -> Unit,
    ) = forEachIndexed { i, r -> r.draw(clip = clip) { draw(i) } }
}
