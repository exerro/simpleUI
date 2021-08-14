package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL46C

@Undocumented
internal class NVGRenderingContext(
    private val nvg: NVGData,
    private val palette: Palette,
    override val id: StaticIdentifier?,
    override val region: Region,
    private val clipRegion: Region,
    private val isRoot: Boolean,
): DrawContext {
    override fun fill(colour: PaletteColour, opacity: Float) {
        // TODO: respect clipping
        val rgb = palette[colour]
        if (isRoot) {
            GL46C.glClearColor(rgb.red, rgb.green, rgb.blue, opacity)
            GL46C.glClear(GL46C.GL_COLOR_BUFFER_BIT)
        }
        else {
            NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgRect(nvg.context, rx, ry, rw, rh)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgFillColor(nvg.context, nvg.colour)
            NanoVG.nvgFill(nvg.context)
        }
    }

    override fun roundedRectangle(
        cornerRadius: Float,
        colour: PaletteColour,
        borderColour: PaletteColour,
        borderWidth: Float
    ) {
        // TODO: respect clipping
        val rgb = palette[colour]
        NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRoundedRect(nvg.context, rx, ry, rw, rh, cornerRadius)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillColor(nvg.context, nvg.colour)
        NanoVG.nvgFill(nvg.context)

        if (borderWidth > 0f) {
            val rgbBorder = palette[borderColour]
            NanoVG.nvgRGBf(rgbBorder.red, rgbBorder.green, rgbBorder.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgRoundedRect(nvg.context, rx + borderWidth / 2 - 1f, ry + borderWidth / 2 - 1f, rw - borderWidth + 2f, rh - borderWidth + 2f, cornerRadius)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
            NanoVG.nvgStrokeWidth(nvg.context, borderWidth)
            NanoVG.nvgStroke(nvg.context)
        }
    }

    override fun shadow(colour: PaletteColour, radius: Float) {
        // TODO: respect clipping
        TODO("not implemented")
    }

    override fun write(
        text: FormattedText<*>,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        wrap: Boolean
    ) {
        // TODO: respect clipping
        NanoVG.nvgTextAlign(nvg.context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(nvg.context, font.lineHeight)
        NanoVG.nvgFontFace(nvg.context, if (font.isMonospaced) "mono" else "sans")

        val spaceBuffer = NVGGlyphPosition.calloc(2)
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "  ", spaceBuffer)
        val indentationSize = (spaceBuffer[1].minx() - spaceBuffer[0].minx()) * (0.5f - horizontalAlignment) * 2

        var indentation = 0
        val textLines = text.lines.map { line ->
            line to line.joinToString("") { when (it) {
                is FormattedText.Segment.LineBreak -> ""
                is FormattedText.Segment.Text -> it.text
                is FormattedText.Segment.Whitespace -> " ".repeat(it.length)
            } }
        }

        val widthsAndPositionedSegments = textLines.flatMap { (formattedLine, lineString) ->
            // TODO: indentation!
            if (lineString.isEmpty()) return@flatMap listOf(0f to emptyList<Pair<Float, FormattedText.Segment.Text<*>>>())

            val buffer = NVGGlyphPosition.calloc(lineString.length + 1)
            NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "$lineString ", buffer)

            var lineWidth = 0f
            var wsWidth = 0f
            var xOffset = 0f
            val result = mutableListOf<Pair<Float, List<Pair<Float,FormattedText.Segment.Text<*>>>>>()
            var positionedSegments = mutableListOf<Pair<Float, FormattedText.Segment.Text<*>>>()
            var charIndex = 0

            for (segment in formattedLine) when (segment) {
                is FormattedText.Segment.LineBreak -> {
                    indentation += segment.relativeIndent
                    break
                }
                is FormattedText.Segment.Whitespace -> {
                    wsWidth += buffer[charIndex + segment.length].minx() - buffer[charIndex].minx()
                    charIndex += segment.length
                }
                is FormattedText.Segment.Text -> {
                    val ww = buffer[charIndex + segment.text.length].minx() - buffer[charIndex].minx()

                    if (wrap && lineWidth + wsWidth + ww > rw) {
                        // wrap
                        result += lineWidth to positionedSegments
                        positionedSegments = mutableListOf()
                        xOffset += lineWidth + wsWidth
                        wsWidth = 0f
                        lineWidth = 0f
                    }

                    positionedSegments.add(buffer[charIndex].minx() - xOffset + indentation * indentationSize to segment)
                    lineWidth += ww + wsWidth
                    wsWidth = 0f
                    charIndex += segment.text.length
                }
            }

            // TODO: word wrapping

            buffer.free()
            result.add(lineWidth to positionedSegments)
            result
        }

        val maxHeight = font.lineHeight * widthsAndPositionedSegments.size
        val maxWidth = widthsAndPositionedSegments.maxOfOrNull { it.first } ?: 0f
        val x0 = rx + (rw - maxWidth) * horizontalAlignment
        var y = ry + (rh - maxHeight) * verticalAlignment

        for ((lineWidth, line) in widthsAndPositionedSegments) {
            val alignOffset = (maxWidth - lineWidth) * horizontalAlignment
            for ((offset, segment) in line) {
                val rgb = palette[segment.colour]
                NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
                NanoVG.nvgFillColor(nvg.context, nvg.colour)
                NanoVG.nvgText(nvg.context, x0 + offset + alignOffset, y, segment.text)
            }

            y += font.lineHeight
        }
    }

    override fun image(
        path: String,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        stretchToFit: Boolean
    ) {
        TODO("not implemented")
    }

    override fun Region.draw(clip: Boolean, id: StaticIdentifier?, mount: MountPoint?, draw: DrawContext.() -> Unit) {
        val drawRegion = if (id == null) this else
            nvg.animation.evaluateRegion(this, clipRegion, id, mount, draw)

        NVGRenderingContext(
            nvg, palette, id,
            drawRegion,
            if (clip) drawRegion else clipRegion,
            false
        ).draw()
    }

    private val rx = region.x
    private val ry = region.y
    private val rw = region.width
    private val rh = region.height
    private val cx = clipRegion.x
    private val cy = clipRegion.y
    private val cw = clipRegion.width
    private val ch = clipRegion.height
}
