package com.exerro.simpleui.internal

import com.exerro.simpleui.Font
import com.exerro.simpleui.Graphics
import com.exerro.simpleui.TextBuffer
import com.exerro.simpleui.UndocumentedInternal
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NanoVG
import java.nio.ByteBuffer
import kotlin.math.max

@UndocumentedInternal
internal class NVGGraphics(
    val context: Long,
    val colour: NVGColor,
    val colour2: NVGColor,
    val monoBuffer: ByteBuffer,
    val sansBuffer: ByteBuffer,
    val imageCache: MutableMap<String, Int>
): Graphics {
    override fun <Colour> wordWrap(
        buffer: TextBuffer<Colour>,
        availableWidth: Float,
        font: Font,
        indentationSize: Int,
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

    override fun textBufferSize(buffer: TextBuffer<*>, font: Font, indentationSize: Int): Pair<Float, Float> {
        setupFont(font)

        val lineHeight = font.lineHeight
        val totalBufferHeight = lineHeight * buffer.lines.size
        val indentationWidth = getIndentationWidth(indentationSize)
        var maxWidth = 0f

        for (line in buffer.lines) {
            if (line.contentSegments.isEmpty()) continue

            val (totalWidth, _) = generateCharacterBounds(concatenateSegments(line.contentSegments))
            val thisIndentationWidth = indentationWidth * line.indentation

            maxWidth = max(maxWidth, totalWidth + thisIndentationWidth)
        }

        return maxWidth to totalBufferHeight
    }

    ////////////////////////////////////////////////////////////////////////////

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
    internal fun getIndentationWidth(
        indentationSize: Int
    ): Float {
        val glyphBuffer = NVGGlyphPosition.calloc(indentationSize)
        NanoVG.nvgTextGlyphPositions(context, 0f, 0f, " ".repeat(indentationSize), glyphBuffer)
        val startX = glyphBuffer.get(0).minx()
        val endX = glyphBuffer.get(indentationSize - 1).maxx()
        glyphBuffer.free()
        return endX - startX
    }

    @UndocumentedInternal
    internal fun generateCharacterBounds(
        lineText: String
    ): Pair<Float, Array<Pair<Float, Float>>> {
        if (lineText.isEmpty()) return 0f to emptyArray()

        val glyphBuffer = NVGGlyphPosition.calloc(lineText.length + 1)
        NanoVG.nvgTextGlyphPositions(context, 0f, 0f, "$lineText ", glyphBuffer)
        val startX = glyphBuffer.get(0).minx()
        val endX = glyphBuffer.get(lineText.length).minx()
        val characterBounds = Array(lineText.length) { index ->
            glyphBuffer.get(index).minx() to glyphBuffer.get(index + 1).minx()
        }

        glyphBuffer.free()

        return (endX - startX) to characterBounds
    }

    @UndocumentedInternal
    internal fun setupFont(font: Font) {
        NanoVG.nvgTextAlign(context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(context, font.lineHeight)
        NanoVG.nvgFontFace(context, if (font.isMonospaced) "mono" else "sans")
    }

    @UndocumentedInternal
    private fun concatenateSegments(
        segments: List<TextBuffer.ContentSegment<*>>
    ): String = segments.joinToString("") { when (it) {
        is TextBuffer.ContentSegment.Text -> it.text
        is TextBuffer.ContentSegment.Whitespace -> " ".repeat(it.length)
    } }
}
