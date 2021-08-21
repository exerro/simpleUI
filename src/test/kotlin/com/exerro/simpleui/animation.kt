package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.extensions.animatedFrom
import com.exerro.simpleui.extensions.animationTo
import com.exerro.simpleui.extensions.withEasing

fun main() {
    val window = GLFWWindowCreator.createWindow("Animation")
    var visible = true
    var targetRegion = Region(0f, 0f, 100f, 100f)
    val helper = AnimationHelper<Region>(
        exit = { v -> Region
            .animationTo(v.copy(x = -v.width))
            .withEasing(Animation.Easing.OUT)
               },
        enter = { v -> Region
            .animationTo(v)
            .withEasing(Animation.Easing.IN)
            .animatedFrom(v.copy(x = -v.width))
                },
    )

    fun redraw() = window.draw {
        fill(Colours.black)

        val animatedRegion = if (visible) helper.animatedValue(targetRegion, Animation.Easing.BETWEEN) else helper.exit()

//        animatedRegion?.draw {
//            fill(Colours.white)
//        }
    }

    window.events
        .filterIsInstance<EKeyPressed>()
        .filter { !it.isRepeat }
        .connect {
            if (it.name == "e") visible = !visible

            targetRegion = Region(
                x = rand(0, 500),
                y = rand(0, 400),
                width = rand(100, 200),
                height = rand(100, 200),
            )
            redraw()
        }

    redraw()

    while (!window.isClosed) GLFWWindowCreator.update()
}

fun rand(a: Int, b: Int) = a + Math.random().toFloat() * (b - a)
