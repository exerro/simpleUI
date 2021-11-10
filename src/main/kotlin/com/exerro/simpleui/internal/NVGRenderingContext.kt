package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL46C
import kotlin.math.max
import kotlin.math.min

/** NanoVG implementation of a [DrawContext]. */
internal class NVGRenderingContext(
    private val nvg: NVGData,
    override val region: Region,
    override val clipRegion: Region,
    private val isRoot: Boolean,
    private val delta: Long,
): DrawContext {
    override fun <T> Animated<T>.component1(): T {
        if (!isFinished) { update(delta) }
        if (!isFinished) { hasDynamicContent = true }
        return currentValue
    }

    override fun fill(colour: Colour) {
        if (isRoot) {
            GL46C.glClearColor(colour.red, colour.green, colour.blue, colour.alpha)
            GL46C.glClear(GL46C.GL_COLOR_BUFFER_BIT)
        }
        else {
            NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgRect(nvg.context, rx, ry, rw, rh)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgFillColor(nvg.context, nvg.colour)
            NanoVG.nvgFill(nvg.context)
        }
    }

    override fun roundedRectangle(
        cornerRadius: Pixels,
        colour: Colour,
        borderColour: Colour,
        borderWidth: Pixels,
    ) {
        val cr = cornerRadius.apply(min(rw, rh))
        val bw = borderWidth.apply(min(rw, rh))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, nvg.colour)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRoundedRect(nvg.context, rx, ry, rw, rh, cr)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillColor(nvg.context, nvg.colour)
        NanoVG.nvgFill(nvg.context)

        if (bw > 0f) {
            NanoVG.nvgRGBf(borderColour.red, borderColour.green, borderColour.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgRoundedRect(nvg.context, rx + bw / 2 - 1f, ry + bw / 2 - 1f, rw - bw + 2f, rh - bw + 2f, cr)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
            NanoVG.nvgStrokeWidth(nvg.context, bw)
            NanoVG.nvgStroke(nvg.context)
        }
    }

    override fun ellipse(
        colour: Colour,
        borderColour: Colour,
        borderWidth: Pixels,
    ) {
        val bw = borderWidth.apply(min(rw, rh))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, nvg.colour)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgEllipse(nvg.context, rx + rw / 2, ry + rh / 2, rw / 2, rh / 2)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillColor(nvg.context, nvg.colour)
        NanoVG.nvgFill(nvg.context)

        if (bw > 0f) {
            NanoVG.nvgRGBf(borderColour.red, borderColour.green, borderColour.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgEllipse(nvg.context, rx + rw / 2 + bw / 2 - 1f, ry + rh / 2 + bw / 2 - 1f, rw / 2 - bw + 2f, rh / 2 - bw + 2f)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
            NanoVG.nvgStrokeWidth(nvg.context, bw)
            NanoVG.nvgStroke(nvg.context)
        }
    }

    override fun shadow(colour: Colour, radius: Pixels, offset: Pixels, cornerRadius: Pixels) {
        val paint = NVGPaint.calloc()
        val dy = offset.apply(min(rw, rh))
        val cr = cornerRadius.apply(min(rw, rh))
        val r = radius.apply(min(rw, rh))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, 1f, nvg.colour)
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, 0f, nvg.colour2)
        NanoVG.nvgBoxGradient(nvg.context, rx, ry + dy, rw, rh, cr, r, nvg.colour, nvg.colour2, paint)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRect(nvg.context, rx - r, ry + dy - r, rw + r * 2, rh + r * 2)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillPaint(nvg.context, paint)
        NanoVG.nvgFill(nvg.context)
        paint.free()
    }

    override fun image(
        path: String,
        tint: Colour?,
        isResource: Boolean,
    ) {
        val image = nvg.imageCache.computeIfAbsent(path) {
            if (isResource) {
                val imageStream = GLFWWindowCreator::class.java.getResourceAsStream(path)!!
                val imageByteArray = imageStream.readAllBytes()
                val imageBuffer = BufferUtils.createByteBuffer(imageByteArray.size)
                imageBuffer.put(imageByteArray)
                imageBuffer.flip()
                NanoVG.nvgCreateImageMem(nvg.context, 0, imageBuffer)
            }
            else {
                NanoVG.nvgCreateImage(nvg.context, path, 0)
            }
        }
        val rgb = tint ?: RGBA(1f, 1f, 1f)
        val paint = NVGPaint.calloc()
        NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, 1f, nvg.colour)
        NanoVG.nvgImagePattern(nvg.context, rx, ry, rw, rh, 0f, image, 1f, paint)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRect(nvg.context, rx, ry, rw, rh)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillPaint(nvg.context, paint)
        NanoVG.nvgFill(nvg.context)
        paint.free()
    }

    override fun <Tag> generateTextBufferTagged(
        font: Font,
        horizontalAlignment: Alignment,
        indentationSize: Int,
        initialIndentation: Int,
        wrap: Boolean,
        writer: TextDrawContext<Tag>.() -> Unit
    ): TextBuffer<Tag> {
        // set NVG font rendering settings
        NanoVG.nvgTextAlign(nvg.context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(nvg.context, font.lineHeight)
        NanoVG.nvgFontFace(nvg.context, if (font.isMonospaced) "mono" else "sans")

        // calculate the width of a space character, and therefore the pixels per indent (tab)
        val spaceBuffer = NVGGlyphPosition.calloc(2)
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "  ", spaceBuffer)
        val whitespaceWidth = spaceBuffer[1].minx() - spaceBuffer[0].minx()
        val pixelsPerIndentation = whitespaceWidth * indentationSize * (0.5f - horizontalAlignment) * 2
        spaceBuffer.free()

        // render the text, using lines afterwards
        val lines = mutableListOf<TextBuffer.Line<Tag>>()
        val colourTracker = MetaColourTracker()
        var indentation = initialIndentation
        var indentationPixels = initialIndentation * pixelsPerIndentation
        var thisLineSegments = mutableListOf<TextBuffer.Segment<Tag>>()

        val ctx = object: TextDrawContext<Tag> {
            override fun lineBreak(relativeIndentation: Int) {
                // note: alignment based horizontal offset resolved later
                lines.add(TextBuffer.Line(indentationPixels, thisLineSegments))
                thisLineSegments = mutableListOf()
                indentation += relativeIndentation
                indentationPixels = indentation * pixelsPerIndentation
            }

            override fun whitespaceTagged(length: Int, tag: Tag) {
                thisLineSegments.add(TextBuffer.Segment(
                    tag = tag,
                    horizontalOffset = 0f, // resolved later
                    textWidth = whitespaceWidth * length,
                    text = " ".repeat(length),
                    textColour = Colours.pureWhite,
                    highlightColour = colourTracker.currentHighlightColour(),
                    strikeThroughColour = colourTracker.currentStrikeThroughColour(),
                    underlineColour = colourTracker.currentUnderlineColour(),
                    isWhitespace = true,
                ))
            }

            override fun textTagged(text: String, colour: Colour, tag: Tag, splitAtSpaces: Boolean) {
                if (text.isEmpty()) return
                if (splitAtSpaces) {
                    val parts = text.split(' ')
                    if (parts[0].isNotEmpty()) textTagged(parts[0], colour, tag, false)

                    for (part in parts.drop(1)) {
                        whitespaceTagged(1, tag)
                        if (part.isNotEmpty()) textTagged(part, colour, tag, false)
                    }
                }
                else {
                    val buffer = NVGGlyphPosition.calloc(text.length + 1)
                    NanoVG.nvgTextGlyphPositions(this@NVGRenderingContext.nvg.context, 0f, 0f, "$text ", buffer)
                    val width = buffer[text.length].minx() - buffer[0].minx()

                    thisLineSegments.add(TextBuffer.Segment(
                        tag = tag,
                        horizontalOffset = 0f, // resolved later
                        textWidth = width,
                        text = text,
                        textColour = colour,
                        highlightColour = colourTracker.currentHighlightColour(),
                        strikeThroughColour = colourTracker.currentStrikeThroughColour(),
                        underlineColour = colourTracker.currentUnderlineColour(),
                        isWhitespace = false,
                    ))
                }
            }

            override fun verticalCursor(colour: Colour) = TODO()
            override fun beginUnderlining(colour: Colour) = colourTracker.pushUnderlineColour(colour)
            override fun stopUnderlining() = colourTracker.popUnderlineColour()
            override fun beginStrikingThrough(colour: Colour) = colourTracker.pushStrikeThroughColour(colour)
            override fun stopStrikingThrough() = colourTracker.popStrikeThroughColour()
            override fun beginHighlighting(colour: Colour) = colourTracker.pushHighlightColour(colour)
            override fun stopHighlighting() = colourTracker.popHighlightColour()
        }

        ctx.writer()
        ctx.lineBreak()

        // word wrap and generate offsets
        val wordWrappedLines = lines.flatMap { line ->
            val segmentsWithOffset = line.segments.fold(0f to emptyList<Pair<Float, TextBuffer.Segment<Tag>>>()) { (offset, lines), segment ->
                (offset + segment.textWidth) to (lines + (offset to segment))
            } .second
            val outputLines = mutableListOf<List<TextBuffer.Segment<Tag>>>()
            var thisLine = mutableListOf<TextBuffer.Segment<Tag>>()
            var offsetAllowance = 0f

            for ((offset, segment) in segmentsWithOffset) {
                val effectiveOffset = offset - offsetAllowance
                if (segment.isWhitespace || effectiveOffset + segment.textWidth < rw) {
                    thisLine.add(segment.copy(horizontalOffset = effectiveOffset)) // offset generation happens here!
                }
                else {
                    outputLines.add(thisLine)
                    thisLine = mutableListOf(segment)
                    offsetAllowance = offset
                }
            }

            outputLines.add(thisLine)

            outputLines.mapIndexed { index, segments ->
                TextBuffer.Line(line.horizontalOffset, segments.dropLastWhile { it.isWhitespace && index < outputLines.lastIndex })
            }
        }

        return TextBuffer(
            font = font,
            totalHeight = font.lineHeight * wordWrappedLines.size,
            maximumWidth = wordWrappedLines.fold(0f) { a, b -> max(a, b.totalWidth) },
            lines = wordWrappedLines.map { line ->
                line.copy(horizontalOffset = line.horizontalOffset + (rw - line.totalWidth) * horizontalAlignment)
            }
        )
    }

    override fun <Tag> writeTextBuffer(buffer: TextBuffer<Tag>, verticalAlignment: Alignment): TextBuffer<Tag> {
        var y = ry + (rh - buffer.totalHeight) * verticalAlignment

        for (line in buffer.lines) {
            if (line.segments.isEmpty()) { y += buffer.font.lineHeight; continue }
            val x = rx + line.horizontalOffset
            // (rw - line.totalWidth) * horizontalAlignment + indent * pixelsPerIndentation

            for (segment in line.segments) {
                if (segment.highlightColour != null) {
                    val rgb = segment.highlightColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + segment.horizontalOffset, y, segment.textWidth, buffer.font.lineHeight)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }

                if (!segment.isWhitespace) run {
                    val rgb = segment.textColour
                    NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgText(nvg.context, x + segment.horizontalOffset, y, segment.text)
                }

                if (segment.underlineColour != null) {
                    val rgb = segment.underlineColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + segment.horizontalOffset, y + buffer.font.lineHeight - 2f, segment.textWidth, 2f)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }

                if (segment.strikeThroughColour != null) {
                    val rgb = segment.strikeThroughColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + segment.horizontalOffset, y + buffer.font.lineHeight * 0.54f, segment.textWidth, 2f)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }
            }

            y += buffer.font.lineHeight
        }

        return buffer
    }

    override fun Region.draw(clip: Boolean, draw: (DrawContext) -> Unit) {
        val drawRegion = this
        val subClipRegion = if (clip) clipRegion intersectionWith drawRegion else clipRegion

        if (clip) {
            if (subClipRegion.width == 0f || subClipRegion.height == 0f) return
            NanoVG.nvgScissor(nvg.context, subClipRegion.x, subClipRegion.y, subClipRegion.width, subClipRegion.height)
        }

        draw(NVGRenderingContext(
            nvg,
            drawRegion,
            subClipRegion,
            false,
            delta,
        ))

        if (clip) {
            NanoVG.nvgScissor(nvg.context, clipRegion.x, clipRegion.y, clipRegion.width, clipRegion.height)
        }
    }

    init {
        NanoVG.nvgScissor(nvg.context, clipRegion.x, clipRegion.y, clipRegion.width, clipRegion.height)
    }

    internal var hasDynamicContent = false
    private val rx = region.x
    private val ry = region.y
    private val rw = region.width
    private val rh = region.height
}
