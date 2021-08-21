package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Greyscale

/** A simplified [DrawContext] used for internal rendering. **/
internal interface DrawContextImpl {
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
        borderColour: Colour,
        borderWidth: Pixels,
    )

    /** Draw an ellipse spanning the full area of the [region]. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun ellipse(
        colour: Colour,
        borderColour: Colour,
        borderWidth: Pixels,
    )

    /** Draw a shadow at the outer-edge of the current [region]. [radius]
     *  controls how far the shadow spreads. [offset] controls vertical offset
     *  downwards. [cornerRadius] gives the shadow rounded corners, and should
     *  match that of [roundedRectangle] when giving a rounded rectangle or
     *  circle a shadow. */
    fun shadow(
        colour: Colour,
        radius: Pixels,
        offset: Pixels,
        cornerRadius: Pixels,
    )

    /** Draw an image stretched to fit the [region]. If [isResource] is true,
     *  the [path] given is treated as a resource path. Otherwise, it is treated
     *  as a file name. [tint] controls tinting, where null indicates no tint
     *  (natural colour). For maintaining aspect ratio when drawing, see
     *  [Region.withAspectRatio]. */
    fun image(
        path: String,
        tint: Colour?,
        isResource: Boolean,
    )

    /** Write complex formatted text, using [writer]. */
    fun write(
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int,
        initialIndentation: Int,
        wrap: Boolean,
        skipRender: Boolean,
        writer: TextDrawContext.() -> Unit,
    ): Region

//    @Undocumented
//    fun <T> animated(
//        from: T, to: T,
//        interpolate: (t: Float, from: T, to: T) -> T,
//        duration: Duration = Duration.seconds(0.25),
//        draw: DrawContext.(value: T) -> Unit,
//    )

    /** Draw content within another region. When [clip] is true, all content
     *  drawn within the callback [draw] is clipped to the region given. */
    fun draw(
        region: Region,
        clip: Boolean = false,
        draw: (DrawContextImpl) -> Unit,
    )
}
