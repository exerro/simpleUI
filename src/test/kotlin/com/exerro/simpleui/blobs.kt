package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.*
import com.exerro.simpleui.ui.hooks.useOnce
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.*
import java.lang.Math.random

fun main() {
    val window = GLFWWindowCreator.createWindow("UI")

    val controller = UIController {
        val (width, setWidth) = useState(512f)

        onDraw {
            fill(model.style[Style.AlternateBackgroundColour])
        }

        withPadding(32.px, 64.px).vdiv(32.px, spacing = 32.px) {
            withHorizontalAlignment(0.5f).button("Toggle width", focused = true) {
                setWidth((512 + random() * 512).toFloat())
            }

            withAlignment(0.5f, 0f).withWidth(width.px).withDecoration {
                shadow(cornerRadius = 16.px)
                roundedRectangle(cornerRadius = 16.px, colour = model.style[Style.BackgroundColour])
            } .withPadding(32.px).flow(
                verticalSpacing = 16.px,
                horizontalSpacing = 16.px,
                verticalRowAlignment = 0f,
                horizontalRowAlignment = 0f,
            ) {
                for (i in 1 .. 32) {
                    animatedRegion {
                        component {
                            val (width, height) = useOnce { (128 + random() * 128) to (64 + random() * 64) }
                            val colour = useOnce { Colours.random() }

                            onDraw {
                                shadow(cornerRadius = 8.px)
                                roundedRectangle(cornerRadius = 8.px, colour = colour)
                            }

                            noChildrenDefineDefaultSize(width.toFloat(), height.toFloat())
                        }
                    }
                }
            }
        }
    }

    controller.events.connect { window.draw { controller.draw(this) } }
    window.events.connect(controller::pushEvent)
    controller.load()

    while (!window.isClosed) GLFWWindowCreator.update()
}
