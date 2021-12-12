package com.exerro.simpleui

import com.exerro.simpleui.experimental.Palette
import com.exerro.simpleui.internal.NVGGraphics
import com.exerro.simpleui.internal.NVGRenderer
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46C
import org.lwjgl.system.MemoryUtil
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/** Implementation of [WindowCreator] using a GLFW+NanoVG backend. */
object GLFWWindowCreator: WindowCreator {
    override fun createWindow(
        title: String
    ): Window {
        val worker = WorkerThread()
        val onEventList = mutableListOf<(WindowEvent) -> Unit>()
        var isClosed = false
        lateinit var nvgGraphics: NVGGraphics
        lateinit var nvgRenderer: NVGRenderer
        var palette: Palette = Palette.Default

        if (windows++ == 0) {
            // initialise GLFW, this is the first window!
            GLFWErrorCallback.createPrint().set()
            GLFW.glfwInit()
        }

        // work out the width we want to use based on the monitor
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
        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE)
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

        fun closeWindow() {
            if (isClosed) return
            isClosed = true
            worker.stop()

            // destroy the window
            GLFW.glfwDestroyWindow(windowID)

            // terminate GLFW if this was the last window
            if (--windows == 0) GLFW.glfwTerminate()
        }

        ////////////////////////////////////////////////////////

        GLFW.glfwSetWindowCloseCallback(windowID) {
            pushEvent(EWindowClosed)
            closeWindow()
        }

        GLFW.glfwSetWindowRefreshCallback(windowID) {
            worker.reloop()
        }

        GLFW.glfwSetKeyCallback(windowID) { _, key, scancode, action, mods ->
            val repeat = action == GLFW.GLFW_REPEAT
            val pressed = action == GLFW.GLFW_PRESS || repeat
            val name = when (key) {
                GLFW.GLFW_KEY_SPACE -> "space"
                GLFW.GLFW_KEY_APOSTROPHE -> "'"
                GLFW.GLFW_KEY_COMMA -> ","
                GLFW.GLFW_KEY_MINUS -> "-"
                GLFW.GLFW_KEY_PERIOD -> "."
                GLFW.GLFW_KEY_SLASH -> "/"
                GLFW.GLFW_KEY_SEMICOLON -> ";"
                GLFW.GLFW_KEY_EQUAL -> "="
                GLFW.GLFW_KEY_GRAVE_ACCENT -> "`"
                GLFW.GLFW_KEY_WORLD_1 -> "world-1"
                GLFW.GLFW_KEY_WORLD_2 -> "world-2"
                GLFW.GLFW_KEY_ESCAPE -> "escape"
                GLFW.GLFW_KEY_ENTER -> "enter"
                GLFW.GLFW_KEY_TAB -> "tab"
                GLFW.GLFW_KEY_BACKSPACE -> "backspace"
                GLFW.GLFW_KEY_INSERT -> "insert"
                GLFW.GLFW_KEY_DELETE -> "delete"
                GLFW.GLFW_KEY_RIGHT -> "right"
                GLFW.GLFW_KEY_LEFT -> "left"
                GLFW.GLFW_KEY_DOWN -> "down"
                GLFW.GLFW_KEY_UP -> "up"
                GLFW.GLFW_KEY_PAGE_UP -> "page-up"
                GLFW.GLFW_KEY_PAGE_DOWN -> "page-down"
                GLFW.GLFW_KEY_HOME -> "home"
                GLFW.GLFW_KEY_END -> "end"
                GLFW.GLFW_KEY_CAPS_LOCK -> "caps-lock"
                GLFW.GLFW_KEY_SCROLL_LOCK -> "scroll-lock"
                GLFW.GLFW_KEY_NUM_LOCK -> "num-lock"
                GLFW.GLFW_KEY_PRINT_SCREEN -> "print-screen"
                GLFW.GLFW_KEY_PAUSE -> "pause"
                GLFW.GLFW_KEY_F1 -> "f1"
                GLFW.GLFW_KEY_F2 -> "f2"
                GLFW.GLFW_KEY_F3 -> "f3"
                GLFW.GLFW_KEY_F4 -> "f4"
                GLFW.GLFW_KEY_F5 -> "f5"
                GLFW.GLFW_KEY_F6 -> "f6"
                GLFW.GLFW_KEY_F7 -> "f7"
                GLFW.GLFW_KEY_F8 -> "f8"
                GLFW.GLFW_KEY_F9 -> "f9"
                GLFW.GLFW_KEY_F10 -> "f10"
                GLFW.GLFW_KEY_F11 -> "f11"
                GLFW.GLFW_KEY_F12 -> "f12"
                GLFW.GLFW_KEY_F13 -> "f13"
                GLFW.GLFW_KEY_F14 -> "f14"
                GLFW.GLFW_KEY_F15 -> "f15"
                GLFW.GLFW_KEY_F16 -> "f16"
                GLFW.GLFW_KEY_F17 -> "f17"
                GLFW.GLFW_KEY_F18 -> "f18"
                GLFW.GLFW_KEY_F19 -> "f19"
                GLFW.GLFW_KEY_F20 -> "f20"
                GLFW.GLFW_KEY_F21 -> "f21"
                GLFW.GLFW_KEY_F22 -> "f22"
                GLFW.GLFW_KEY_F23 -> "f23"
                GLFW.GLFW_KEY_F24 -> "f24"
                GLFW.GLFW_KEY_F25 -> "f25"
                GLFW.GLFW_KEY_KP_0 -> "kp-0"
                GLFW.GLFW_KEY_KP_1 -> "kp-1"
                GLFW.GLFW_KEY_KP_2 -> "kp-2"
                GLFW.GLFW_KEY_KP_3 -> "kp-3"
                GLFW.GLFW_KEY_KP_4 -> "kp-4"
                GLFW.GLFW_KEY_KP_5 -> "kp-5"
                GLFW.GLFW_KEY_KP_6 -> "kp-6"
                GLFW.GLFW_KEY_KP_7 -> "kp-7"
                GLFW.GLFW_KEY_KP_8 -> "kp-8"
                GLFW.GLFW_KEY_KP_9 -> "kp-9"
                GLFW.GLFW_KEY_KP_ENTER -> "kp-enter"
                GLFW.GLFW_KEY_KP_EQUAL -> "kp-equals"
                GLFW.GLFW_KEY_LEFT_SHIFT -> "shift"
                GLFW.GLFW_KEY_LEFT_CONTROL -> "ctrl"
                GLFW.GLFW_KEY_LEFT_ALT -> "alt"
                GLFW.GLFW_KEY_LEFT_SUPER -> "super"
                GLFW.GLFW_KEY_RIGHT_SHIFT -> "right-shift"
                GLFW.GLFW_KEY_RIGHT_CONTROL -> "right-ctrl"
                GLFW.GLFW_KEY_RIGHT_ALT -> "right-alt"
                GLFW.GLFW_KEY_RIGHT_SUPER -> "right-super"
                GLFW.GLFW_KEY_MENU -> "menu"
                else -> GLFW.glfwGetKeyName(key, scancode) ?: "unknown"
            }
            val modifiers = setOfNotNull(
                KeyModifier.Control.takeIf { mods and GLFW.GLFW_MOD_CONTROL != 0 },
                KeyModifier.Alt.takeIf { mods and GLFW.GLFW_MOD_ALT != 0 },
                KeyModifier.Shift.takeIf { mods and GLFW.GLFW_MOD_SHIFT != 0 },
                KeyModifier.Super.takeIf { mods and GLFW.GLFW_MOD_SUPER != 0 },
            )

            if (pressed) pushEvent(EKeyPressed(name, scancode, repeat, modifiers))
            else pushEvent(EKeyReleased(name, scancode, modifiers))
        }

        GLFW.glfwSetCharCallback(windowID) { _, codepoint ->
            val content = String(intArrayOf(codepoint), 0, 1)
            pushEvent(ETextInput(content))
        }

        // TODO: hook window mouse events
        // TODO: disable rendering when window not visible (e.g. minimised)

        ////////////////////////////////////////////////////////

        worker.onFinish = {
            val w = IntArray(1)
            val h = IntArray(1)

            GLFW.glfwGetFramebufferSize(windowID, w, h)
            GL46C.glViewport(0, 0, w[0], h[0])
            GL46C.glClearColor(0f, 0f, 0f, 1f)
            GL46C.glClear(GL46C.GL_COLOR_BUFFER_BIT)
            GLFW.glfwSwapBuffers(windowID)
            nvgGraphics.colour.free()
            nvgGraphics.colour2.free()

            for (image in nvgGraphics.imageCache.values) {
                NanoVG.nvgDeleteImage(nvgGraphics.context, image)
            }

            NanoVGGL3.nvgDelete(nvgGraphics.context)
            GL.setCapabilities(null)
        }

        worker.start("SimpleUI Render Thread [$title]") {
            GLFW.glfwMakeContextCurrent(windowID)
            GL.createCapabilities()
            val context = NanoVGGL3.nvgCreate(0)
            if (context == MemoryUtil.NULL) return@start
            val colour = NVGColor.calloc()
            val colour2 = NVGColor.calloc()

            val monoStream = this::class.java.getResourceAsStream("fonts/inconsolata/Inconsolata.otf")!!
            val monoByteArray = monoStream.readAllBytes()
            val monoBuffer = BufferUtils.createByteBuffer(monoByteArray.size)
            monoBuffer.put(monoByteArray)
            monoBuffer.flip()
            NanoVG.nvgCreateFontMem(context, "mono", monoBuffer, 1)

            val sansStream = this::class.java.getResourceAsStream("fonts/open-sans/OpenSans-Regular.ttf")!!
            val sansByteArray = sansStream.readAllBytes()
            val sansBuffer = BufferUtils.createByteBuffer(sansByteArray.size)
            sansBuffer.put(sansByteArray)
            sansBuffer.flip()
            NanoVG.nvgCreateFontMem(context, "sans", sansBuffer, 1)

            nvgGraphics = NVGGraphics(context, colour, colour2, monoBuffer, sansBuffer, mutableMapOf())
            nvgRenderer = NVGRenderer(nvgGraphics)
        }

        ////////////////////////////////////////////////////////

        return object: Window {
            override val isClosed get() = isClosed
            override var palette
                get() = palette
                set(value) { palette = value }

            override val timeSource = object: TimeSource {
                override fun markNow() = object: TimeMark() {
                    private val createdAt = System.nanoTime()
                    override fun elapsedNow() = Duration.nanoseconds(System.nanoTime() - createdAt)
                }
            }
            override val createdAt = timeSource.markNow()
            override val events = EventBus<WindowEvent> { onEvent ->
                synchronized(onEventList) { onEventList.add(onEvent) }
                EventBus.Connection {
                    synchronized(onEventList) { onEventList.remove(onEvent) }
                }
            }

            override fun draw(layers: LayerComposition, onDraw: DrawContext.(deltaTime: Duration) -> Unit) {
                var lastFrame = System.nanoTime()
                val width = IntArray(1)
                val height = IntArray(1)

                worker.loop {
                    val time = System.nanoTime()
                    GLFW.glfwGetFramebufferSize(windowID, width, height)
                    val r = Region(0f, 0f, width[0].toFloat(), height[0].toFloat())

                    val (d) = DrawContext.buffer(nvgGraphics, Layer.Default, r, r, nvgRenderer) {
                        val time = System.nanoTime()
                        onDraw(Duration.nanoseconds(time - lastFrame))
                        lastFrame = time
                    }

                    NanoVG.nvgBeginFrame(nvgGraphics.context, width[0].toFloat(), height[0].toFloat(), 1f)
                    GL46C.glViewport(0, 0, width[0], height[0])

                    layers.run {
                        object: LayerComposition.Context {
                            override fun drawLayer(layer: Layer) {
                                for (ds in d.layers[layer] ?: emptyList()) {
                                    nvgRenderer.submit(layer, ds.clipRegion, ds.drawCalls)
                                }
                            }
                        } .draw()
                    }

                    lastFrame = time
                    NanoVG.nvgEndFrame(nvgGraphics.context)
                    GLFW.glfwSwapBuffers(windowID)

                    d.hasDynamicContent
                }
            }

            override fun redraw() {
                worker.reloop()
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

    private class WorkerThread {
        var onFinish: () -> Unit = {}

        fun start(name: String, init: () -> Unit) {
            val c = CountDownLatch(1)

            thread = thread(start = true, name = name, isDaemon = true) {
                init()
                c.countDown()

                while (running) {
                    try {
                        if (dirty) { dirty = false; dirty = nextLoop() || dirty }
                        Thread.sleep(8)
                    }
                    catch (e: InterruptedException) { /* do nothing */ }
                    catch (e: Throwable) {
                        e.printStackTrace(System.err)
                    }
                }

                onFinish()
            }

            c.await()
        }

        fun loop(fn: () -> Boolean) {
            nextLoop = fn
            reloop()
        }

        fun reloop() {
            dirty = true
            thread.interrupt()
        }

        fun stop() {
            running = false
            thread.join()
        }

        private lateinit var thread: Thread
        private var dirty = true
        private var nextLoop: () -> Boolean = { false }
        private var running = true
    }

    ////////////////////////////////////////////////////////////////////////////

    /** Number of windows visible. Used to terminate GLFW when the last window
     *  is closed. */
    private var windows = 0
}
