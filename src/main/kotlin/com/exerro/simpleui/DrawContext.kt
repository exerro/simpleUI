package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Greyscale
import kotlin.time.Duration

/** A [DrawContext] provides drawing capabilities. The context is associated
 *  with a [region], representing the area on-screen where content is drawn.
 *  Various methods draw content within this [region], such as [fill], and
 *  [roundedRectangle].
 *  Initially, [region] represents the window content area. To draw in a
 *  different [Region], the [region] field can be used to derive a sub-region
 *  then use [draw] to draw within that instead.
 *
 *  **Warning**: unless explicitly stated otherwise, using context members after
 *  a frame has been drawn will lead to undefined behaviour. An example of this
 *  might be keeping a reference to the context in a callback passed elsewhere.
 *  By the time that callback is invoked, the window may no longer even exist
 *  and calling graphics methods will likely result in low-level crashes, or at
 *  least unexpected behaviour. */
@DrawContextDSL
interface DrawContext {
    /** Area on-screen where content is drawn relative to. */
    val region: Region

    /** Area on-screen where content is visible. */
    val clipRegion: Region

    /** Register that dynamic content has been drawn in this region, allowing
     *  the rendering implementation to queue another draw. Use this for
     *  animations and content which changes with each render.
     *  Can optionally pass [changesIn] to specify when the content will change.
     *  When [changesIn] is null, the content is assumed to change
     *  "immediately" e.g. for high refresh rate continuous animations. */
    fun dynamicContent(
        changesIn: Duration? = null
    )

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

    /** Return a copy of [buffer] word-wrapped to fit the [availableWidth]. */
    fun <Colour> wordWrap(
        buffer: TextBuffer<Colour>,
        font: Font = Font.default,
        indentationSize: Int = 4,
        availableWidth: Float = region.width,
    ): TextBuffer<Colour>

    /** Return a [Region] which will span the area used when rendering [buffer]
     *  using [write] with the same parameters. Note: this includes any space
     *  used for indentation. */
    fun textBufferBounds(
        buffer: TextBuffer<*>,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
    ): Region

    /** Draw a [buffer] of text to the screen. */
    fun write(
        buffer: TextBuffer<Colour>,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
    )

    /** Create a [TextBuffer] representing [text], [colour] and [wordWrap] and
     *  draw that to the screen using [write]. When [wordWrap] is false, a
     *  single text segment is added to the buffer. Otherwise, the text is
     *  added to the buffer using [TextBufferBuilder.emitTextSegments]
     *  (splitting on newlines and spaces) and word wrapped before the buffer
     *  is passed to [write]. */
    fun write(
        text: String,
        colour: Colour,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
        wordWrap: Boolean = false,
    )

    /** Draw content within another region. When [clip] is true, all content
     *  drawn within the callback [draw] is clipped to the region given. */
    fun <T> Region.draw(
        clip: Boolean = false,
        draw: DrawContext.() -> T,
    ): T

    /** Shorthand for drawing a list of regions. See [DrawContext.draw]. [draw]
     *  callback receives an additional parameter for the index of the region
     *  being drawn. */
    fun List<Region>.draw(
        clip: Boolean = false,
        draw: DrawContext.(index: Int) -> Unit,
    ) = forEachIndexed { i, r -> r.draw(clip = clip) { draw(i) } }
}
