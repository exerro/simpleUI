package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.experimental.animation.AnimatedValue
import com.exerro.simpleui.experimental.animation.Animation
import com.exerro.simpleui.experimental.animationTo
import com.exerro.simpleui.experimental.withEasing

fun main() {
    val window = GLFWWindowCreator.createWindow("Animation")
    var visible = true
    val target = AnimatedValue(Region(0f, 0f, 100f, 100f))

    fun redraw() = window.draw { deltaTime ->
        fill(Colours.black)

        if (!target.isFinished) { target.update(deltaTime.inWholeNanoseconds) }
        if (!target.isFinished) dynamicContent()

        val animatedRegion = target.currentValue

        withRegion(animatedRegion) {
            fill(Colours.white)
        }
    }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { !it.isRepeat }
        .connect {
            if (it.name == "e") visible = !visible

            if (visible) {
                val enterTo = Region(
                    x = rand(0, 500),
                    y = rand(0, 400),
                    width = rand(100, 200),
                    height = rand(100, 200),
                )
                if (target.isFinished) target.reset(enterTo.copy(x = -enterTo.width))
                target.reset(animation = Region.animationTo(enterTo) withEasing Animation.Easing.IN)
            }
            else {
                target.reset(animation = Region.animationTo(
                    target = target.currentValue.copy(x = -target.currentValue.width)
                ) withEasing Animation.Easing.OUT)
            }

            redraw()
        }

    redraw()

    while (!window.isClosed) GLFWWindowCreator.update()
}

fun rand(a: Int, b: Int) = a + Math.random().toFloat() * (b - a)
