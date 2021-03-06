package com.exerro.simpleui

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Greyscale
import kotlin.time.Duration

@Undocumented
/** A [DrawContext] provides drawing capabilities. The context is associated
 *  with a [region], representing the area on-screen where content is drawn.
 *  Various methods draw content within this [region], such as [fill], and
 *  [roundedRectangle].
 *  Initially, [region] represents the window content area. To draw in a
 *  different [Region], the [region] field can be used to derive a sub-region
 *  then use [withRegion] to draw within that instead.
 *
 *  **Warning**: unless explicitly stated otherwise, using context members after
 *  a frame has been drawn will lead to undefined behaviour. An example of this
 *  might be keeping a reference to the context in a callback passed elsewhere.
 *  By the time that callback is invoked, the window may no longer even exist
 *  and calling graphics methods will likely result in low-level crashes, or at
 *  least unexpected behaviour. */
@DrawContextDSL
interface DrawContext {
    /** Graphics associated with this [DrawContext]. */
    val graphics: Graphics

    /** Layer being drawn to. */
    val layer: Layer

    /** Area representing the full region of this draw context. */
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
        changesIn: Duration = Duration.ZERO
    )

    /** Fill the region with a [colour]. */
    fun fill(
        colour: Colour,
        region: Region = this.region,
    )

    /** Draw a rounded rectangle, optionally providing a border. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun roundedRectangle(
        cornerRadius: Pixels,
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
        region: Region = this.region,
    )

    /** Draw an ellipse spanning the full area of the [region]. Note that the
     *  border is always fully opaque, regardless of alpha. */
    fun ellipse(
        colour: Colour,
        borderColour: Colour = colour,
        borderWidth: Pixels = 0.px,
        region: Region = this.region,
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
        region: Region = this.region,
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
        region: Region = this.region,
    )

    /** Draw a [buffer] of text to the screen. */
    fun write(
        buffer: TextBuffer<Colour>,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        indentationSize: Int = 4,
        region: Region = this.region,
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
        region: Region = this.region,
    ) = write(
        buffer = graphics.wordWrap(TextBufferBuilder(text = text, colour = colour, splitSegments = wordWrap), font = font, indentationSize = indentationSize, availableWidth = region.width),
        font = font,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
        indentationSize = indentationSize
    )

    /** Draw content using [draw] on the [layer] provided. Layers describe an
     *  ordering to drawn content. See [Window.draw]. */
    fun <T> withLayer(
        layer: Layer,
        draw: DrawContext.() -> T,
    ): T

    /** Draw content within another region. When [clip] is true, all content
     *  drawn within the callback [draw] is clipped to the region given. */
    fun <T> withRegion(
        region: Region,
        clip: Boolean = false,
        draw: DrawContext.() -> T
    ): T

    /** Shorthand for drawing a list of regions. See [withRegion]. [draw]
     *  callback receives an additional parameter for the index of the region
     *  being drawn. */
    fun <T> withRegions(
        regions: List<Region>,
        clip: Boolean = false,
        draw: DrawContext.(index: Int) -> T
    ): List<T> = regions.mapIndexed { i, v -> withRegion(v, clip) { draw(i) } }

    /** Represents a set of draw calls that have been collected but yet
     *  executed, grouped by [Layer]. */
    data class DeferredDrawCalls(
        val contentChangesDynamicallyIn: Duration,
        val layers: Map<Layer, List<DeferredLayer>>,
    ) {
        /** A sequence of draw calls in a given [Layer], within a defined
         *  [clipRegion]. */
        data class DeferredLayer(
            val clipRegion: Region,
            val drawCalls: List<DrawContextRenderer.DeferredDrawCall>,
        )
    }

    companion object {
        /** Buffer a [draw] function, returning a [DeferredDrawCalls] instance
         *  which can be used to actually draw the content. */
        internal fun <T> buffer(
            graphics: Graphics,
            layer: Layer,
            drawRegion: Region,
            clipRegion: Region,
            impl: DrawContextRenderer,
            draw: DrawContext.() -> T
        ): Pair<DeferredDrawCalls, T> {
            var currentDrawRegion = drawRegion
            var contentChangesDynamicallyIn = Duration.INFINITE
            val deferredDrawCalls = mutableListOf<DrawContextRenderer.DeferredDrawCall>()
            val result = mutableMapOf<Layer, MutableList<DeferredDrawCalls.DeferredLayer>>()

            fun addCurrentStuff() {
                if (deferredDrawCalls.isNotEmpty()) {
                    result.computeIfAbsent(layer) { mutableListOf() } += DeferredDrawCalls.DeferredLayer(
                        clipRegion = clipRegion,
                        drawCalls = deferredDrawCalls.toList() // copy
                    )
                    deferredDrawCalls.clear()
                }
            }

            val context = object: DrawContext {
                override val graphics = graphics
                override val layer = layer
                override val region get() = currentDrawRegion
                override val clipRegion = clipRegion

                override fun dynamicContent(changesIn: Duration) {
                    if (changesIn == Duration.INFINITE) return

                    contentChangesDynamicallyIn = minOf(changesIn, contentChangesDynamicallyIn)
                }

                override fun fill(colour: Colour, region: Region) {
                    deferredDrawCalls += impl.fill(region, colour)
                }

                override fun roundedRectangle(
                    cornerRadius: Pixels,
                    colour: Colour,
                    borderColour: Colour,
                    borderWidth: Pixels,
                    region: Region,
                ) {
                    deferredDrawCalls += impl.roundedRectangle(region, cornerRadius, colour, borderColour, borderWidth)
                }

                override fun ellipse(colour: Colour, borderColour: Colour, borderWidth: Pixels, region: Region) {
                    deferredDrawCalls += impl.ellipse(region, colour, borderColour, borderWidth)
                }

                override fun shadow(colour: Colour, radius: Pixels, offset: Pixels, cornerRadius: Pixels, region: Region) {
                    deferredDrawCalls += impl.shadow(region, colour, radius, offset, cornerRadius)
                }

                override fun image(path: String, tint: Colour?, isResource: Boolean, region: Region) {
                    deferredDrawCalls += impl.image(region, path, tint, isResource)
                }

                override fun write(
                    buffer: TextBuffer<Colour>,
                    font: Font,
                    horizontalAlignment: Alignment,
                    verticalAlignment: Alignment,
                    indentationSize: Int,
                    region: Region,
                ) {
                    deferredDrawCalls += impl.write(region, buffer, font, horizontalAlignment, verticalAlignment, indentationSize)

                    for (line in buffer.lines) {
                        for (cursor in line.cursors) {
                            dynamicContent(cursor.timeTillVisibilityChanged())
                        }
                    }
                }

                override fun <T> withLayer(layer: Layer, draw: DrawContext.() -> T): T {
                    addCurrentStuff()

                    val (additionalContent, r) = buffer(graphics, layer, currentDrawRegion, clipRegion, impl, draw)

                    dynamicContent(additionalContent.contentChangesDynamicallyIn)

                    for ((l, ds) in additionalContent.layers) {
                        result.computeIfAbsent(l) { mutableListOf() } .addAll(ds)
                    }

                    return r
                }

                override fun <T> withRegion(region: Region, clip: Boolean, draw: DrawContext.() -> T): T {
                    return if (clip) {
                        addCurrentStuff()

                        val (additionalContent, r) = buffer(graphics, layer, region, region, impl, draw)

                        dynamicContent(additionalContent.contentChangesDynamicallyIn)

                        for ((l, ds) in additionalContent.layers) {
                            result.computeIfAbsent(l) { mutableListOf() } .addAll(ds)
                        }

                        r
                    }
                    else {
                        val oldDrawRegion = currentDrawRegion
                        currentDrawRegion = region
                        val r = draw()
                        currentDrawRegion = oldDrawRegion
                        r
                    }
                }
            }

            val r = context.draw()

            addCurrentStuff()

            return DeferredDrawCalls(contentChangesDynamicallyIn, result) to r
        }
    }
}
