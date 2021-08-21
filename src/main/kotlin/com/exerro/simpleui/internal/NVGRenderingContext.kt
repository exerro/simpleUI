package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.RGBA
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL46C
import kotlin.math.min

/** NanoVG implementation of a [DrawContext]. */
internal class NVGRenderingContext(
    private val nvg: NVGData,
    override val region: Region,
    override val clipRegion: Region,
    private val isRoot: Boolean,
): DrawContextImpl {
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

    override fun write(
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int,
        initialIndentation: Int,
        wrap: Boolean,
        writer: TextDrawContext.() -> Unit
    ) {
        val lines = mutableListOf<Pair<Int, List<Triple<MutableTextLine.Segment, Float, Float>>>>()
        val spaceBuffer = NVGGlyphPosition.calloc(2)

        NanoVG.nvgTextAlign(nvg.context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(nvg.context, font.lineHeight)
        NanoVG.nvgFontFace(nvg.context, if (font.isMonospaced) "mono" else "sans")
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "  ", spaceBuffer)

        val pixelsPerIndentation = indentationSize * (spaceBuffer[1].minx() - spaceBuffer[0].minx()) * (0.5f - horizontalAlignment) * 2
        spaceBuffer.free()

        fun drawLine(
            allText: String,
            segments: List<MutableTextLine.Segment>,
            cursors: List<MutableTextLine.Cursor>,
            indentation: Int,
        ) {
            val buffer = NVGGlyphPosition.calloc(allText.length)
            NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, allText, buffer)

            var wrapOffset = 0f
            var thisLine = mutableListOf<Triple<MutableTextLine.Segment, Float, Float>>()
            val allWrappedLines = mutableListOf(indentation to thisLine)

            // TODO: do something with cursors :((

            for (segment in segments) {
                val startX = buffer[segment.startCharIndex].minx() - wrapOffset
                val endX = when (segment.startCharIndex + segment.text.length >= allText.length) {
                    true -> buffer.last().maxx()
                    else -> buffer[segment.startCharIndex + segment.text.length].minx()
                } - wrapOffset

                if (wrap && endX > rw && !segment.isWhitespace) {
                    wrapOffset += startX
                    thisLine = mutableListOf(Triple(segment, 0f, endX - startX))
                    allWrappedLines.add(indentation to thisLine)
                }
                else {
                    thisLine.add(Triple(segment, startX, endX))
                }
            }

            for ((_, line) in allWrappedLines) {
                while (line.lastOrNull()?.first?.isWhitespace == true)
                    line.removeLast()
            }

            lines.addAll(allWrappedLines)
            buffer.free()
        }

        var indentation = initialIndentation
        val tl = MutableTextLine()
        val ctx = object: TextDrawContext {
            override fun lineBreak(relativeIndentation: Int) {
                val (line, segments, cursors) = tl.finishLine()

                drawLine(line, segments, cursors, indentation)
                indentation += relativeIndentation
            }

            override fun whitespace(length: Int) {
                tl.pushWhitespace(length)
            }

            override fun text(text: String, colour: Colour, splitAtSpaces: Boolean) {
                if (text.isEmpty()) return
                if (splitAtSpaces) {
                    val parts = text.split(' ')
                    if (parts[0].isNotEmpty()) tl.pushText(parts[0], colour)

                    for (part in parts.drop(1)) {
                        tl.pushWhitespace(1)
                        if (part.isNotEmpty()) tl.pushText(part, colour)
                    }
                }
                else
                    tl.pushText(text, colour)
            }

            override fun verticalCursor(colour: Colour) {
                tl.pushCursor(colour)
            }

            override fun beginUnderlining(colour: Colour) {
                tl.pushUnderlineColour(colour)
            }

            override fun stopUnderlining() {
                tl.popUnderlineColour()
            }

            override fun beginStrikingThrough(colour: Colour) {
                tl.pushStrikeThroughColour(colour)
            }

            override fun stopStrikingThrough() {
                tl.popStrikeThroughColour()
            }

            override fun beginHighlighting(colour: Colour) {
                tl.pushHighlightColour(colour)
            }

            override fun stopHighlighting() {
                tl.popHighlightColour()
            }
        }

        ctx.writer()
        ctx.lineBreak()

        var y = ry + (rh - font.lineHeight * lines.size) * verticalAlignment

        for ((indent, line) in lines) {
            if (line.isEmpty()) { y += font.lineHeight; continue }
            val x0 = line.first().second
            val x1 = line.last().third
            val x = rx + (rw - x1 + x0) * horizontalAlignment + indent * pixelsPerIndentation

            for ((segment, sx0, sx1) in line) {
                if (segment.highlightColour != null) {
                    val rgb = segment.highlightColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + sx0 - x0, y, sx1 - sx0, font.lineHeight)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }

                run {
                    val rgb = segment.textColour
                    NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgText(nvg.context, x + sx0 - x0, y, segment.text)
                }

                if (segment.underlineColour != null) {
                    val rgb = segment.underlineColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + sx0 - x0, y + font.lineHeight - 2f, sx1 - sx0, 2f)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }

                if (segment.strikeThroughColour != null) {
                    val rgb = segment.strikeThroughColour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgBeginPath(nvg.context)
                    NanoVG.nvgRect(nvg.context, x + sx0 - x0, y + font.lineHeight * 0.54f, sx1 - sx0, 2f)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgFill(nvg.context)
                }
            }

            y += font.lineHeight
        }
    }

    override fun draw(region: Region, clip: Boolean, draw: (DrawContextImpl) -> Unit) {
        val drawRegion = region
        val subClipRegion = if (clip) clipRegion intersectionWith drawRegion else clipRegion

        if (clip) {
            if (subClipRegion.width == 0f || subClipRegion.height == 0f) return
            NanoVG.nvgScissor(nvg.context, subClipRegion.x, subClipRegion.y, subClipRegion.width, subClipRegion.height)
        }

        draw(NVGRenderingContext(
            nvg,
            drawRegion,
            subClipRegion,
            false
        ))

        if (clip) {
            NanoVG.nvgScissor(nvg.context, clipRegion.x, clipRegion.y, clipRegion.width, clipRegion.height)
        }
    }

    init {
        NanoVG.nvgScissor(nvg.context, clipRegion.x, clipRegion.y, clipRegion.width, clipRegion.height)
    }

    private val rx = region.x
    private val ry = region.y
    private val rw = region.width
    private val rh = region.height
}
