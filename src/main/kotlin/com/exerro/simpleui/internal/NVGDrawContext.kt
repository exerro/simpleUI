package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.RGBA
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

/** NanoVG implementation of a [DrawContext]. */
internal class NVGDrawContext(
    private val graphics: NVGGraphics,
): DrawContextImplementor {
    override fun submit(layer: Layer, clipRegion: Region, calls: Iterable<DrawContextImplementor.DeferredDrawCall>) {
        NanoVG.nvgScissor(graphics.context, clipRegion.x, clipRegion.y, clipRegion.width, clipRegion.height)
        for (call in calls) call.draw()
    }

    override fun fill(
        region: Region,
        colour: Colour
    ) = DrawContextImplementor.DeferredDrawCall {
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, graphics.colour)
        NanoVG.nvgBeginPath(graphics.context)
        NanoVG.nvgRect(graphics.context, region.x, region.y, region.width, region.height)
        NanoVG.nvgClosePath(graphics.context)
        NanoVG.nvgFillColor(graphics.context, graphics.colour)
        NanoVG.nvgFill(graphics.context)
    }

    override fun roundedRectangle(
        region: Region,
        cornerRadius: Pixels,
        colour: Colour,
        borderColour: Colour,
        borderWidth: Pixels,
    ) = DrawContextImplementor.DeferredDrawCall {
        val cr = cornerRadius.apply(min(region.width, region.height))
        val bw = borderWidth.apply(min(region.width, region.height))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, graphics.colour)
        NanoVG.nvgBeginPath(graphics.context)
        NanoVG.nvgRoundedRect(graphics.context, region.x, region.y, region.width, region.height, cr)
        NanoVG.nvgClosePath(graphics.context)
        NanoVG.nvgFillColor(graphics.context, graphics.colour)
        NanoVG.nvgFill(graphics.context)

        if (bw > 0f) {
            NanoVG.nvgRGBf(borderColour.red, borderColour.green, borderColour.blue, graphics.colour)
            NanoVG.nvgBeginPath(graphics.context)
            NanoVG.nvgRoundedRect(graphics.context, region.x + bw / 2 - 1f, region.y + bw / 2 - 1f, region.width - bw + 2f, region.height - bw + 2f, cr)
            NanoVG.nvgClosePath(graphics.context)
            NanoVG.nvgStrokeColor(graphics.context, graphics.colour)
            NanoVG.nvgStrokeWidth(graphics.context, bw)
            NanoVG.nvgStroke(graphics.context)
        }
    }

    override fun ellipse(
        region: Region,
        colour: Colour,
        borderColour: Colour,
        borderWidth: Pixels,
    ) = DrawContextImplementor.DeferredDrawCall {
        val bw = borderWidth.apply(min(region.width, region.height))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, colour.alpha, graphics.colour)
        NanoVG.nvgBeginPath(graphics.context)
        NanoVG.nvgEllipse(graphics.context, region.x + region.width / 2, region.y + region.height / 2, region.width / 2, region.height / 2)
        NanoVG.nvgClosePath(graphics.context)
        NanoVG.nvgFillColor(graphics.context, graphics.colour)
        NanoVG.nvgFill(graphics.context)

        if (bw > 0f) {
            NanoVG.nvgRGBf(borderColour.red, borderColour.green, borderColour.blue, graphics.colour)
            NanoVG.nvgBeginPath(graphics.context)
            NanoVG.nvgEllipse(graphics.context, region.x + region.width / 2 + bw / 2 - 1f, region.y + region.height / 2 + bw / 2 - 1f, region.width / 2 - bw + 2f, region.height / 2 - bw + 2f)
            NanoVG.nvgClosePath(graphics.context)
            NanoVG.nvgStrokeColor(graphics.context, graphics.colour)
            NanoVG.nvgStrokeWidth(graphics.context, bw)
            NanoVG.nvgStroke(graphics.context)
        }
    }

    override fun shadow(
        region: Region,
        colour: Colour,
        radius: Pixels,
        offset: Pixels,
        cornerRadius: Pixels,
    ) = DrawContextImplementor.DeferredDrawCall {
        val paint = NVGPaint.calloc()
        val dy = offset.apply(min(region.width, region.height))
        val cr = cornerRadius.apply(min(region.width, region.height))
        val r = radius.apply(min(region.width, region.height))
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, 1f, graphics.colour)
        NanoVG.nvgRGBAf(colour.red, colour.green, colour.blue, 0f, graphics.colour2)
        NanoVG.nvgBoxGradient(graphics.context, region.x, region.y + dy, region.width, region.height, cr, r, graphics.colour, graphics.colour2, paint)
        NanoVG.nvgBeginPath(graphics.context)
        NanoVG.nvgRect(graphics.context, region.x - r, region.y + dy - r, region.width + r * 2, region.height + r * 2)
        NanoVG.nvgClosePath(graphics.context)
        NanoVG.nvgFillPaint(graphics.context, paint)
        NanoVG.nvgFill(graphics.context)
        paint.free()
    }

    override fun image(
        region: Region,
        path: String,
        tint: Colour?,
        isResource: Boolean,
    ) = DrawContextImplementor.DeferredDrawCall {
        val image = graphics.imageCache.computeIfAbsent(path) {
            if (isResource) {
                val imageStream = GLFWWindowCreator::class.java.getResourceAsStream(path)!!
                val imageByteArray = imageStream.readAllBytes()
                val imageBuffer = BufferUtils.createByteBuffer(imageByteArray.size)
                imageBuffer.put(imageByteArray)
                imageBuffer.flip()
                NanoVG.nvgCreateImageMem(graphics.context, 0, imageBuffer)
            }
            else {
                NanoVG.nvgCreateImage(graphics.context, path, 0)
            }
        }
        val rgba = tint ?: RGBA(1f, 1f, 1f)
        val paint = NVGPaint.calloc()

        NanoVG.nvgRGBAf(rgba.red, rgba.green, rgba.blue, rgba.alpha, graphics.colour)
        NanoVG.nvgImagePattern(graphics.context, region.x, region.y, region.width, region.height, 0f, image, 1f, paint)
        paint.innerColor(graphics.colour)
        NanoVG.nvgBeginPath(graphics.context)
        NanoVG.nvgRect(graphics.context, region.x, region.y, region.width, region.height)
        NanoVG.nvgClosePath(graphics.context)
        // TODO: NanoVG.nvgFillColor(graphics.context, graphics.colour)
        NanoVG.nvgFillPaint(graphics.context, paint)
        NanoVG.nvgFill(graphics.context)
        paint.free()
    }

    override fun write(
        region: Region,
        buffer: TextBuffer<Colour>,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int
    ) = DrawContextImplementor.DeferredDrawCall {
        graphics.setupFont(font)

        val lineHeight = font.lineHeight
        val totalBufferHeight = lineHeight * buffer.lines.size
        val indentationWidth = graphics.getIndentationWidth(indentationSize)
        var y = floor(region.y + (region.height - totalBufferHeight) * verticalAlignment)

        for (line in buffer.lines) {
            val (totalWidth, characterBounds) = graphics.generateCharacterBounds(concatenateSegments(line.contentSegments))

            if (characterBounds.isEmpty()) continue

            val thisIndentationWidth = indentationWidth * line.indentation
            val thisIndentationOffset = thisIndentationWidth * (1 - horizontalAlignment * 2)
            val x = floor(region.x + (region.width - totalWidth) * horizontalAlignment + thisIndentationOffset) - characterBounds[0].first
            var characterOffset = 0

            if (line.contentSegments.isEmpty() && line.decorationSegments.isEmpty()) {
                y += lineHeight
                continue
            }

            fun drawDecoration(segment: TextBuffer.DecorationSegment<Colour>) {
                val rgba = segment.colour
                val (sy, sh) = getDecorationBounds(segment.decoration, y, lineHeight)
                val sx = characterBounds[segment.offset].first
                val sw = characterBounds[segment.offset + segment.length - 1].second - sx

                NanoVG.nvgRGBAf(rgba.red, rgba.green, rgba.blue, rgba.alpha, graphics.colour)
                NanoVG.nvgBeginPath(graphics.context)
                NanoVG.nvgRect(graphics.context, x + sx, sy, sw, sh)
                NanoVG.nvgClosePath(graphics.context)
                NanoVG.nvgFillColor(graphics.context, graphics.colour)
                NanoVG.nvgFill(graphics.context)
            }

            for (segment in line.decorationSegments.filter { it.decoration.background }) {
                drawDecoration(segment)
            }

            for (segment in line.contentSegments) {
                if (segment is TextBuffer.ContentSegment.Text && segment.text.isNotEmpty()) {
                    val rgba = segment.colour
                    NanoVG.nvgRGBAf(rgba.red, rgba.green, rgba.blue, rgba.alpha, graphics.colour)
                    NanoVG.nvgFillColor(graphics.context, graphics.colour)
                    NanoVG.nvgText(graphics.context, x + characterBounds[characterOffset].first, y, segment.text)
                }

                characterOffset += when (segment) {
                    is TextBuffer.ContentSegment.Text -> segment.text.length
                    is TextBuffer.ContentSegment.Whitespace -> segment.length
                }
            }

            for (segment in line.decorationSegments.filter { !it.decoration.background }) {
                drawDecoration(segment)
            }

            for (cursor in line.cursors) {
                if (!cursor.isVisible()) continue

                val cx = round(when (cursor.offset) {
                    0 -> characterBounds[0].first - 1
                    characterBounds.size -> characterBounds[cursor.offset - 1].second
                    else -> (characterBounds[cursor.offset].first + characterBounds[cursor.offset - 1].second) / 2f - 1
                })

                val rgba = cursor.colour
                NanoVG.nvgRGBAf(rgba.red, rgba.green, rgba.blue, rgba.alpha, graphics.colour)
                NanoVG.nvgBeginPath(graphics.context)
                NanoVG.nvgRect(graphics.context, x + cx, y + 2f, 2f, lineHeight - 4f)
                NanoVG.nvgClosePath(graphics.context)
                NanoVG.nvgFillColor(graphics.context, graphics.colour)
                NanoVG.nvgFill(graphics.context)
            }

            y += lineHeight
        }
    }

    @UndocumentedInternal
    private fun concatenateSegments(
        segments: List<TextBuffer.ContentSegment<*>>
    ): String = segments.joinToString("") { when (it) {
        is TextBuffer.ContentSegment.Text -> it.text
        is TextBuffer.ContentSegment.Whitespace -> " ".repeat(it.length)
    } }

    @UndocumentedInternal
    private fun getDecorationBounds(
        decoration: TextBuffer.Decoration,
        y: Float,
        lineHeight: Float,
    ): Pair<Float, Float> = when (decoration) {
        TextBuffer.Decoration.Underline -> floor(y + lineHeight - 2f) to 2f
        TextBuffer.Decoration.Strikethrough -> floor(y + lineHeight * 0.55f) to 2f
        TextBuffer.Decoration.Highlight -> floor(y) to ceil(lineHeight)
    }
}
