package com.exerro.simpleui

import com.exerro.simpleui.internal.AnimationHelper
import com.exerro.simpleui.internal.NVGData
import com.exerro.simpleui.internal.NVGRenderingContext
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL46C
import org.lwjgl.system.MemoryUtil
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.concurrent.thread

/** Implementation of [WindowCreator] using a GLFW+NanoVG backend. */
object GLFWWindowCreator: WindowCreator {
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

        fun submitRedraw(contentChanged: Boolean): Unit = renderFunction.let { rf -> renderQueue.offer {
            val width = IntArray(1)
            val height = IntArray(1)
            GLFW.glfwGetFramebufferSize(windowID, width, height)
            NanoVG.nvgBeginFrame(nvgData.context, width[0].toFloat(), height[0].toFloat(), 1f)
            GL46C.glViewport(0, 0, width[0], height[0])
            val r = Region(0f, 0f, width[0].toFloat(), height[0].toFloat())
            nvgData.animation.beginFrame(allowAnimations = contentChanged)
            NVGRenderingContext(nvgData, null, r, r, true).rf()
            val (anyAnimating, exitAnimations) = nvgData.animation.endFrame()

            for (d in exitAnimations) {
                val ctx = NVGRenderingContext(nvgData, null, d.region, d.clipRegion, false)
                d.draw(ctx)
            }

            if (anyAnimating && contentChanged && rf == renderFunction) submitRedraw(true)
            NanoVG.nvgEndFrame(nvgData.context)
            GLFW.glfwSwapBuffers(windowID)
            true
        } }

        fun closeWindow() {
            if (isClosed) return
            isClosed = true
            renderQueue.offer {
                val width = IntArray(1)
                val height = IntArray(1)

                GLFW.glfwGetFramebufferSize(windowID, width, height)
                GL46C.glViewport(0, 0, width[0], height[0])
                GL46C.glClearColor(0f, 0f, 0f, 1f)
                GL46C.glClear(GL46C.GL_COLOR_BUFFER_BIT)
                GLFW.glfwSwapBuffers(windowID)
                nvgData.colour.free()
                nvgData.colour2.free()

                for (image in nvgData.imageCache.values) {
                    NanoVG.nvgDeleteImage(nvgData.context, image)
                }

                NanoVGGL3.nvgDelete(nvgData.context)
                GL.setCapabilities(null)
                GLFW.glfwDestroyWindow(windowID)

                // terminate GLFW if this was the last window
                if (--windows == 0) GLFW.glfwTerminate()

                // stop the render thread
                false
            }
        }

        ////////////////////////////////////////////////////////

        // TODO: hook window events

        GLFW.glfwSetWindowCloseCallback(windowID) {
            pushEvent(EWindowClosed)
            closeWindow()
        }

        GLFW.glfwSetWindowRefreshCallback(windowID) {
            submitRedraw(false)
        }

        GLFW.glfwSetKeyCallback(windowID) { _, key, scancode, action, mods ->
            val repeat = action == GLFW.GLFW_REPEAT
            val pressed = action == GLFW.GLFW_PRESS || repeat
            val name = GLFW.glfwGetKeyName(key, scancode) ?: when (key) {
                GLFW.GLFW_KEY_UP -> "up"
                GLFW.GLFW_KEY_DOWN -> "down"
                GLFW.GLFW_KEY_SPACE -> "space"
                // TODO!
                else -> "unknown"
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

        ////////////////////////////////////////////////////////

        renderQueue.offer {
            GLFW.glfwMakeContextCurrent(windowID)
            GL.createCapabilities()
            val context = NanoVGGL3.nvgCreate(0)
            if (context == MemoryUtil.NULL) return@offer false
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

            nvgData = NVGData(context, AnimationHelper(), colour, colour2, monoBuffer, sansBuffer, mutableMapOf())
            true
        }

        submitRedraw(true)

        ////////////////////////////////////////////////////////

        return object: Window {
            override val isClosed get() = isClosed
            override var palette
                get() = palette
                set(value) { palette = value }

            override val events = EventBus<WindowEvent> { onEvent ->
                synchronized(onEventList) { onEventList.add(onEvent) }
                EventBus.Connection {
                    synchronized(onEventList) { onEventList.remove(onEvent) }
                }
            }

            override fun draw(onDraw: DrawContext.() -> Unit) {
                renderFunction = onDraw
                submitRedraw(true)
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

    /** Create a thread running tasks from a queue, and return that queue. */
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

    ////////////////////////////////////////////////////////////////////////////

    /** Number of windows visible. Used to terminate GLFW when the last window
     *  is closed. */
    private var windows = 0
}
