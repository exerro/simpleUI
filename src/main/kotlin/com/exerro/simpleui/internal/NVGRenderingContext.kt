package com.exerro.simpleui.internal

import com.exerro.simpleui.*
import org.lwjgl.BufferUtils
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.opengl.GL46C
import kotlin.math.min

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
        cornerRadius: Pixels,
        colour: PaletteColour,
        borderColour: PaletteColour,
        borderWidth: Pixels,
        opacity: Float,
    ) {
        val rgb = palette[colour]
        val cr = cornerRadius.apply(min(rw, rh))
        val bw = borderWidth.apply(min(rw, rh))
        NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, opacity, nvg.colour)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRoundedRect(nvg.context, rx, ry, rw, rh, cr)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillColor(nvg.context, nvg.colour)
        NanoVG.nvgFill(nvg.context)

        if (bw > 0f) {
            val rgbBorder = palette[borderColour]
            NanoVG.nvgRGBf(rgbBorder.red, rgbBorder.green, rgbBorder.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgRoundedRect(nvg.context, rx + bw / 2 - 1f, ry + bw / 2 - 1f, rw - bw + 2f, rh - bw + 2f, cr)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
            NanoVG.nvgStrokeWidth(nvg.context, bw)
            NanoVG.nvgStroke(nvg.context)
        }
    }

    override fun ellipse(
        colour: PaletteColour,
        borderColour: PaletteColour,
        borderWidth: Pixels,
        opacity: Float,
    ) {
        val rgb = palette[colour]
        val bw = borderWidth.apply(min(rw, rh))
        NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, opacity, nvg.colour)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgEllipse(nvg.context, rx + rw / 2, ry + rh / 2, rw / 2, rh / 2)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillColor(nvg.context, nvg.colour)
        NanoVG.nvgFill(nvg.context)

        if (bw > 0f) {
            val rgbBorder = palette[borderColour]
            NanoVG.nvgRGBf(rgbBorder.red, rgbBorder.green, rgbBorder.blue, nvg.colour)
            NanoVG.nvgBeginPath(nvg.context)
            NanoVG.nvgEllipse(nvg.context, rx + rw / 2 + bw / 2 - 1f, ry + rh / 2 + bw / 2 - 1f, rw / 2 - bw + 2f, rh / 2 - bw + 2f)
            NanoVG.nvgClosePath(nvg.context)
            NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
            NanoVG.nvgStrokeWidth(nvg.context, bw)
            NanoVG.nvgStroke(nvg.context)
        }
    }

    override fun shadow(colour: PaletteColour, radius: Pixels, offset: Pixels, cornerRadius: Pixels) {
        val rgb = palette[colour]
        val paint = NVGPaint.calloc()
        val dy = offset.apply(min(rw, rh))
        val cr = cornerRadius.apply(min(rw, rh))
        val r = radius.apply(min(rw, rh))
        NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, 1f, nvg.colour)
        NanoVG.nvgRGBAf(rgb.red, rgb.green, rgb.blue, 0f, nvg.colour2)
        NanoVG.nvgBoxGradient(nvg.context, rx, ry + dy, rw, rh, cr, r, nvg.colour, nvg.colour2, paint)
        NanoVG.nvgBeginPath(nvg.context)
        NanoVG.nvgRect(nvg.context, rx - r, ry + dy - r, rw + r * 2, rh + r * 2)
        NanoVG.nvgClosePath(nvg.context)
        NanoVG.nvgFillPaint(nvg.context, paint)
        NanoVG.nvgFill(nvg.context)
        paint.free()
    }

    override fun write(
        text: FormattedText<*>,
        font: Font,
        horizontalAlignment: Alignment,
        verticalAlignment: Alignment,
        wrap: Boolean
    ) {
        NanoVG.nvgTextAlign(nvg.context, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        NanoVG.nvgFontSize(nvg.context, font.lineHeight)
        NanoVG.nvgFontFace(nvg.context, if (font.isMonospaced) "mono" else "sans")

        val spaceBuffer = NVGGlyphPosition.calloc(2)
        NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "  ", spaceBuffer)
        val indentationSize = (spaceBuffer[1].minx() - spaceBuffer[0].minx()) * (0.5f - horizontalAlignment) * 2 * 4

        var indentation = 0
        val textLines = text.lines.map { line ->
            line to line.joinToString("") { when (it) {
                is FormattedText.Segment.LineBreak -> ""
                is FormattedText.Segment.Text -> it.text
                is FormattedText.Segment.Whitespace -> " ".repeat(it.length)
            } }
        }

        val widthsAndPositionedSegments = textLines.flatMap { (formattedLine, lineString) ->
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
        tint: PaletteColour?,
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
        val rgb = tint?.let { palette[it] } ?: RGB(1f, 1f, 1f)
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

    override fun Region.draw(clip: Boolean, id: StaticIdentifier?, mount: MountPoint?, draw: DrawContext.() -> Unit) {
        val drawRegion = if (id == null) this else
            nvg.animation.evaluateRegion(this, clipRegion, id, mount, draw)
        val subClipRegion = if (clip) clipRegion intersectionWith drawRegion else clipRegion

        if (clip) {
            if (subClipRegion.width == 0f || subClipRegion.height == 0f) return
            NanoVG.nvgScissor(nvg.context, subClipRegion.x, subClipRegion.y, subClipRegion.width, subClipRegion.height)
        }

        NVGRenderingContext(
            nvg, palette, id,
            drawRegion,
            subClipRegion,
            false
        ).draw()

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
