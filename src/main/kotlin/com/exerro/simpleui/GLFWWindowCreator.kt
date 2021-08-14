package com.exerro.simpleui

import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGGlyphPosition
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46C
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread


@Undocumented
object GLFWWindowCreator: WindowCreator {
    @Undocumented
    override fun createWindow(
        title: String
    ): Window {
        val renderQueue = createWorkerThread("Render [$title]")
        var renderFunction: DrawContext.() -> Unit = {}
        val onEventList = mutableListOf<(WindowEvent) -> Unit>()
        var isClosed = false
        lateinit var nvgData: NVGData
        var palette: Palette = Palette.Default

        if (windows++ == 0) {
            // initialise GLFW, this is the first window!
            GLFWErrorCallback.createPrint().set()
            GLFW.glfwInit()
        }

        // work out the width we wanna use based on the monitor
        val monitor = GLFW.glfwGetPrimaryMonitor()
        val (width, height) = when (monitor == MemoryUtil.NULL) {
            true -> 1080 to 720
            else -> {
                val videoMode = GLFW.glfwGetVideoMode(monitor)
                if (videoMode == null) 1080 to 720
                else videoMode.width() to videoMode.height()
            }
        }

        // create the window, with a few hints
        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE)
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4)

        val windowID = GLFW.glfwCreateWindow(
            width - 128, height - 128, title, MemoryUtil.NULL, MemoryUtil.NULL)

        if (windowID == MemoryUtil.NULL) {
            throw IllegalStateException("Could not create window.")
        }

        ////////////////////////////////////////////////////////

        fun pushEvent(event: WindowEvent) {
            val callbacks = synchronized(onEventList) { onEventList.map { it } }
            callbacks.forEach { it(event) }
        }

        fun submitRedraw() = renderFunction.let { rf -> renderQueue.offer {
            val width = IntArray(1)
            val height = IntArray(1)
            GLFW.glfwGetFramebufferSize(windowID, width, height)
            NanoVG.nvgBeginFrame(nvgData.context, width[0].toFloat(), height[0].toFloat(), 1f)
            redraw(nvgData, palette, width[0], height[0], rf)
            NanoVG.nvgEndFrame(nvgData.context)
            GLFW.glfwSwapBuffers(windowID)
            true
        } }

        fun closeWindow() {
            nvgData.colour.free()
            NanoVGGL3.nvgDelete(nvgData.context)
            GL.setCapabilities(null)
            GLFW.glfwDestroyWindow(windowID)
            isClosed = true
            renderQueue.offer { false }
            // terminate GLFW if this was the last window
            if (--windows == 0) GLFW.glfwTerminate()
        }

        ////////////////////////////////////////////////////////

        // TODO: hook window events

        GLFW.glfwSetWindowCloseCallback(windowID) {
            pushEvent(EWindowClosed)
            closeWindow()
        }

        GLFW.glfwSetWindowRefreshCallback(windowID) {
            submitRedraw()
        }

        ////////////////////////////////////////////////////////

        renderQueue.offer {
            GLFW.glfwMakeContextCurrent(windowID)
            GL.createCapabilities()
            val context = NanoVGGL3.nvgCreate(0)
            if (context == MemoryUtil.NULL) return@offer false
            val colour = NVGColor.calloc()

            val monoStream = this::class.java.getResourceAsStream("/com/exerro/fonts/inconsolata/Inconsolata.otf")
            val monoByteArray = monoStream.readAllBytes()
            val monoBuffer = BufferUtils.createByteBuffer(monoByteArray.size)
            monoBuffer.put(monoByteArray)
            monoBuffer.flip()
            NanoVG.nvgCreateFontMem(context, "mono", monoBuffer, 1)

            val sansStream = this::class.java.getResourceAsStream("/com/exerro/fonts/open-sans/OpenSans-Regular.ttf")
            val sansByteArray = sansStream.readAllBytes()
            val sansBuffer = BufferUtils.createByteBuffer(sansByteArray.size)
            sansBuffer.put(sansByteArray)
            sansBuffer.flip()

            NanoVG.nvgCreateFontMem(context, "sans", sansBuffer, 1)
            NanoVG.nvgCreateFontMem(context, "mono", monoBuffer, 1)

            nvgData = NVGData(context, colour, monoBuffer, sansBuffer)
            true
        }

        submitRedraw()

        ////////////////////////////////////////////////////////

        return object: Window {
            override val isClosed get() = isClosed
            override var palette
                get() = palette
                set(value) { palette = value }

            override val events = Window.EventBus { onEvent ->
                synchronized(onEventList) { onEventList.add(onEvent) }
                Window.EventBus.Connection {
                    synchronized(onEventList) { onEventList.remove(onEvent) }
                }
            }

            override fun draw(onDraw: DrawContext.() -> Unit) {
                renderFunction = onDraw
                submitRedraw()
            }

            override fun close() {
                closeWindow()
            }
        }
    }

    override fun update() {
        if (windows > 0) {
            GLFW.glfwPollEvents()
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private fun redraw(
        nvg: NVGData,
        palette: Palette,
        width: Int,
        height: Int,
        fn: DrawContext.() -> Unit
    ) {
        fun createContext(
            rx: Float, ry: Float,
            rw: Float, rh: Float,
            cx: Float, cy: Float,
            cw: Float, ch: Float,
            isRoot: Boolean,
        ): DrawContext = object: DrawContext {
            override val region = Region(rx, ry, rw, rh)

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
                cornerRadius: Float,
                colour: PaletteColour,
                borderColour: PaletteColour,
                borderWidth: Float
            ) {
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
                    NanoVG.nvgRoundedRect(nvg.context, rx, ry, rw, rh, cornerRadius)
                    NanoVG.nvgClosePath(nvg.context)
                    NanoVG.nvgStrokeColor(nvg.context, nvg.colour)
                    NanoVG.nvgStrokeWidth(nvg.context, borderWidth)
                    NanoVG.nvgStroke(nvg.context)
                }
            }

            override fun shadow(colour: PaletteColour, radius: Float) {
//                TODO("not implemented")
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

                val textLines = text.lines.map { line ->
                    line to line.joinToString("") { when (it) {
                        is FormattedText.Segment.LineBreak -> ""
                        is FormattedText.Segment.Text -> it.text
                        is FormattedText.Segment.Whitespace -> " ".repeat(it.length)
                    } }
                }

                val widthsAndPositionedSegments = textLines.flatMap { (formattedLine, lineString) ->
                    if (lineString.isEmpty()) return@flatMap listOf(0f to emptyList<Pair<Float, FormattedText.Segment.Text<*>>>())

                    val buffer = NVGGlyphPosition.calloc(lineString.length)
                    NanoVG.nvgTextGlyphPositions(nvg.context, 0f, 0f, "$lineString", buffer)

                    val totalWidth = buffer[lineString.length - 1].maxx() - buffer[0].minx()
                    val positionedSegments = mutableListOf<Pair<Float, FormattedText.Segment.Text<*>>>()
                    var x = 0

                    for (segment in formattedLine) when (segment) {
                        is FormattedText.Segment.LineBreak -> break
                        is FormattedText.Segment.Whitespace -> x += segment.length
                        is FormattedText.Segment.Text -> {
                            positionedSegments.add(buffer[x].minx() to segment)
                            x += segment.text.length
                        }
                    }

                    // TODO: word wrapping

                    buffer.free()
                    listOf(totalWidth to positionedSegments)
                }

                val maxHeight = font.lineHeight * widthsAndPositionedSegments.size
                val maxWidth = widthsAndPositionedSegments.maxOfOrNull { it.first } ?: 0f
                val x0 = rx + (rw - maxWidth) * horizontalAlignment
                var y = ry + (rh - maxHeight) * verticalAlignment

                for ((_, line) in widthsAndPositionedSegments) {
                    for ((offset, segment) in line) {
                        val rgb = palette[segment.colour]
                        NanoVG.nvgRGBf(rgb.red, rgb.green, rgb.blue, nvg.colour)
                        NanoVG.nvgFillColor(nvg.context, nvg.colour)
                        NanoVG.nvgText(nvg.context, x0 + offset, y, segment.text)
                    }

                    y += font.lineHeight
                }
            }

            override fun write(
                text: String,
                colour: PaletteColour,
                font: Font,
                horizontalAlignment: Alignment,
                verticalAlignment: Alignment,
                wrap: Boolean
            ) = super.write(text, colour, font, horizontalAlignment, verticalAlignment, wrap)

            override fun image(
                path: String,
                horizontalAlignment: Alignment,
                verticalAlignment: Alignment,
                stretchToFit: Boolean
            ) {
//                TODO("not implemented")
            }

            override fun Region.draw(clip: Boolean, draw: DrawContext.() -> Unit) {
                createContext(
                    x, y, this.width, this.height,
                    if (clip) x else cx, if (clip) y else cy,
                    if (clip) this.width else cw, if (clip) this.height else ch,
                    false
                ).draw()
            }
        }

        GL46C.glViewport(0, 0, width, height)
        createContext(
            0f, 0f, width.toFloat(), height.toFloat(),
            0f, 0f, width.toFloat(), height.toFloat(),
            true,
        ).fn()
    }

    private fun createWorkerThread(name: String, capacity: Int = 4): Queue<() -> Boolean> {
        val queue = ArrayBlockingQueue<() -> Boolean>(capacity)

        thread(start = true, name = name, isDaemon = true) {
            while (true) {
                try {
                    if (!queue.take().invoke()) break
                }
                catch (e: Throwable) {
                    e.printStackTrace(System.err)
                }
            }
        }

        return queue
    }

    private class NVGData(
        val context: Long,
        val colour: NVGColor,
        val monoBuffer: ByteBuffer,
        val sansBuffer: ByteBuffer,
    )

    ////////////////////////////////////////////////////////////////////////////

    private var windows = 0
}
