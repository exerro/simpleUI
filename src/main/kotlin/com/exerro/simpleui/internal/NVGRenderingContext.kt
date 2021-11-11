package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.colour.RGBA
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL46C
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

/** NanoVG implementation of a [DrawContext]. */
internal class NVGRenderingContext(
    private val nvg: NVGData,
    override val region: Region,
    override val clipRegion: Region,
    private val isRoot: Boolean,
    private val delta: Long,
): DrawContext {
    override fun dynamicContent(changesIn: Duration?) {
        val currentDynamicTime = dynamicTime
        hasDynamicContent = true
        dynamicTime = when {
            currentDynamicTime == null -> null
            changesIn == null -> null
            else -> minOf(currentDynamicTime, changesIn)
        }
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

    override fun <Colour> wordWrap(
        buffer: TextBuffer<Colour>,
        font: Font,
        indentationSize: Int,
        availableWidth: Float,
    ): TextBuffer<Colour> {
        setupFont(font)

        val outputLines = mutableListOf<TextBuffer.Line<Colour>>()
        var changesMade = false
        val indentationWidth = getIndentationWidth(indentationSize)

        for (line in buffer.lines) {
            val thisIndentationWidth = indentationWidth * line.indentation
            val segments = line.contentSegments
            val (totalWidth, characterBounds) = generateCharacterBounds(concatenateSegments(segments))

            if (thisIndentationWidth + totalWidth <= availableWidth) {
                outputLines += line
                continue
            }

            changesMade = true

            var currentSegmentIndex = 0
            var currentSegmentCharacterOffset = 0

            // skip initial whitespace
            while (currentSegmentIndex <= segments.lastIndex && segments[currentSegmentIndex] is TextBuffer.ContentSegment.Whitespace) {
                val segmentLength = (segments[currentSegmentIndex] as TextBuffer.ContentSegment.Whitespace).length
                currentSegmentCharacterOffset += segmentLength
                ++currentSegmentIndex
            }

            while (true) {
                val wrappedLine = wordWrapExtractLineBounds(
                    segments = segments,
                    initialSegmentIndex = currentSegmentIndex,
                    initialSegmentCharacterOffset = currentSegmentCharacterOffset,
                    characterBounds = characterBounds,
                    availableWidth = availableWidth - thisIndentationWidth
                )

                outputLines += trimLine(line, currentSegmentIndex, wrappedLine.nextLineSegmentIndex, currentSegmentCharacterOffset, wrappedLine.currentLineLastCharacterOffset)

                if (wrappedLine.nextLineSegmentIndex == segments.size) break

                currentSegmentIndex = wrappedLine.nextLineSegmentIndex
                currentSegmentCharacterOffset = wrappedLine.nextLineSegmentCharacterOffset
            }
        }

        return when (changesMade) {
            true -> TextBuffer(outputLines)
            else -> buffer
        }
    }

    override fun textBufferBounds(
        buffer: TextBuffer<*>,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int,
    ): Region {
        setupFont(font)

        val lineHeight = font.lineHeight
        val totalBufferHeight = lineHeight * buffer.lines.size
        val indentationWidth = getIndentationWidth(indentationSize)
        val y = floor(ry + (rh - totalBufferHeight) * verticalAlignment)
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE

        for (line in buffer.lines) {
            if (line.contentSegments.isEmpty()) continue

            val (totalWidth, characterBounds) = generateCharacterBounds(concatenateSegments(line.contentSegments))
            val thisIndentationWidth = indentationWidth * line.indentation
            val thisIndentationOffset = thisIndentationWidth * (1 - horizontalAlignment * 2)
            val x = floor(rx + (rw - totalWidth) * horizontalAlignment + thisIndentationOffset) - characterBounds[0].first

            minX = min(minX, x + characterBounds[0].first - thisIndentationOffset)
            maxX = max(maxX, x + characterBounds.last().second)
        }

        return Region(minX, y, maxX - minX + 1, totalBufferHeight)
    }

    override fun write(
        buffer: TextBuffer<Colour>,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int
    ) {
        setupFont(font)

        val lineHeight = font.lineHeight
        val totalBufferHeight = lineHeight * buffer.lines.size
        val indentationWidth = getIndentationWidth(indentationSize)
        var y = floor(ry + (rh - totalBufferHeight) * verticalAlignment)

        for (line in buffer.lines) {
            val (totalWidth, characterBounds) = generateCharacterBounds(concatenateSegments(line.contentSegments))
            val thisIndentationWidth = indentationWidth * line.indentation
            val thisIndentationOffset = thisIndentationWidth * (1 - horizontalAlignment * 2)
            val x = floor(rx + (rw - totalWidth) * horizontalAlignment + thisIndentationOffset) - characterBounds[0].first
            var characterOffset = 0

            if (line.contentSegments.isEmpty() && line.decorationSegments.isEmpty()) {
                y += lineHeight
                continue
            }

            fun drawDecoration(segment: TextBuffer.DecorationSegment<Colour>) {
                println(segment)
                val rgba = segment.colour
                val (sy, sh) = getDecorationBounds(segment.decoration, y, lineHeight)
                val sx = characterBounds[segment.offset].first
                val sw = characterBounds[segment.offset + segment.length - 1].second - sx
                NanoVG.nvgRGBAf(rgba.red, rgba.green, rgba.blue, rgba.alpha, nvg.colour)
                NanoVG.nvgBeginPath(nvg.context)
                NanoVG.nvgRect(nvg.context, x + sx, sy, sw, sh)
                NanoVG.nvgClosePath(nvg.context)
                NanoVG.nvgFillColor(nvg.context, nvg.colour)
                NanoVG.nvgFill(nvg.context)
            }

            for (segment in line.decorationSegments.filter { it.decoration.background }) {
                drawDecoration(segment)
            }

            for (segment in line.contentSegments) {
                if (segment is TextBuffer.ContentSegment.Text) {
                    val rgb = segment.colour
                    NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, rgb.alpha, nvg.colour)
                    NanoVG.nvgFillColor(nvg.context, nvg.colour)
                    NanoVG.nvgText(nvg.context, x + characterBounds[characterOffset].first, y, segment.text)
                }

                characterOffset += when (segment) {
                    is TextBuffer.ContentSegment.Text -> segment.text.length
                    is TextBuffer.ContentSegment.Whitespace -> segment.length
                }
            }

            for (segment in line.decorationSegments.filter { !it.decoration.background }) {
                drawDecoration(segment)
            }

            y += lineHeight
        }
    }

    override fun write(
        text: String,
        colour: Colour,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        indentationSize: Int,
        wordWrap: Boolean
    ) = write(
        buffer = wordWrap(TextBufferBuilder(text = text, colour = colour, splitSegments = wordWrap), font = font, indentationSize = indentationSize),
        font = font,
        horizontalAlignment = horizontalAlignment,
        verticalAlignment = verticalAlignment,
        indentationSize = indentationSize
    )

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

    @UndocumentedInternal
    private data class WordWrapExtractedLineData(
        val nextLineSegmentIndex: Int,
        val nextLineSegmentCharacterOffset: Int,
        val currentLineLastCharacterOffset: Int,
    )

    @UndocumentedInternal
    private fun <Colour> wordWrapExtractLineBounds(
        segments: List<TextBuffer.ContentSegment<Colour>>,
        initialSegmentIndex: Int,
        initialSegmentCharacterOffset: Int,
        characterBounds: Array<Pair<Float, Float>>,
        availableWidth: Float,
    ): WordWrapExtractedLineData {
        var segmentCharacterOffset = initialSegmentCharacterOffset + segments[initialSegmentIndex].length
        var endOfLineCharacterOffset = segmentCharacterOffset - 1
        val startX = characterBounds[initialSegmentCharacterOffset].first

        for (index in initialSegmentIndex + 1 until segments.size) {
            val segment = segments[index]

            if (segment !is TextBuffer.ContentSegment.Text) {
                segmentCharacterOffset += segment.length
                continue
            }

            if (characterBounds[segmentCharacterOffset + segment.length - 1].second - startX > availableWidth) {
                return WordWrapExtractedLineData(
                    nextLineSegmentIndex = index,
                    nextLineSegmentCharacterOffset = segmentCharacterOffset,
                    currentLineLastCharacterOffset = endOfLineCharacterOffset,
                )
            }

            segmentCharacterOffset += segment.length
            endOfLineCharacterOffset = segmentCharacterOffset - 1
        }

        return WordWrapExtractedLineData(
            nextLineSegmentIndex = segments.size,
            nextLineSegmentCharacterOffset = segmentCharacterOffset,
            currentLineLastCharacterOffset = endOfLineCharacterOffset,
        )
    }

    @UndocumentedInternal
    private fun <Colour> trimLine(
        line: TextBuffer.Line<Colour>,
        initialSegmentIndex: Int,
        finalSegmentIndex: Int,
        initialSegmentCharacterOffset: Int,
        finalSegmentCharacterOffset: Int,
    ): TextBuffer.Line<Colour> {
        val contentSegments = (initialSegmentIndex until finalSegmentIndex)
            .map(line.contentSegments::get)
            .dropLastWhile { it !is TextBuffer.ContentSegment.Text }
        val decorationSegments = line.decorationSegments.mapNotNull { when {
            it.offset > finalSegmentIndex -> null
            it.offset + it.length - 1 < initialSegmentIndex -> null
            it.offset < initialSegmentCharacterOffset && it.offset + it.length - 1 > finalSegmentCharacterOffset -> it.copy(
                offset = initialSegmentCharacterOffset,
                length = finalSegmentCharacterOffset - initialSegmentCharacterOffset + 1)
            it.offset < initialSegmentCharacterOffset -> it.copy(
                offset = initialSegmentCharacterOffset,
                length = it.offset + it.length - initialSegmentCharacterOffset)
            it.offset + it.length - 1 > finalSegmentCharacterOffset -> it.copy(
                length = finalSegmentCharacterOffset - it.offset + 1)
            else -> it
        } }
            .map { it.copy(offset = it.offset - initialSegmentCharacterOffset) }

        return line.copy(contentSegments = contentSegments, decorationSegments = decorationSegments.toSet())
    }

    @UndocumentedInternal
    private fun getIndentationWidth(
        indentationSize: Int
    ): Float {
        val glyphBuffer = NVGGlyphPosition.calloc(indentationSize)
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, " ".repeat(indentationSize), glyphBuffer)
        val startX = glyphBuffer.get(0).minx()
        val endX = glyphBuffer.get(indentationSize - 1).maxx()
        glyphBuffer.free()
        return endX - startX
    }

    @UndocumentedInternal
    private fun generateCharacterBounds(
        lineText: String
    ): Pair<Float, Array<Pair<Float, Float>>> {
        if (lineText.isEmpty()) return 0f to emptyArray()

        val glyphBuffer = NVGGlyphPosition.calloc(lineText.length + 1)
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "$lineText ", glyphBuffer)
        val startX = glyphBuffer.get(0).minx()
        val endX = glyphBuffer.get(lineText.length).minx()
        val characterBounds = Array(lineText.length) { index ->
            glyphBuffer.get(index).minx() to glyphBuffer.get(index + 1).minx()
        }

        glyphBuffer.free()

        return (endX - startX) to characterBounds
    }

    private fun setupFont(font: Font) {
        NanoVG.nvgTextAlign(nvg.context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(nvg.context, font.lineHeight)
        NanoVG.nvgFontFace(nvg.context, if (font.isMonospaced) "mono" else "sans")
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

    internal var dynamicTime = null as Duration?
    internal var hasDynamicContent = false
    private val rx = region.x
    private val ry = region.y
    private val rw = region.width
    private val rh = region.height
}
