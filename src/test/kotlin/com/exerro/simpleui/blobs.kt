package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.*
import com.exerro.simpleui.ui.extensions.noChildrenDefineDefaultSize
import com.exerro.simpleui.ui.hooks.useOnce
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.*
import java.lang.Math.random

fun blobsController() = UIController {
    val (width, setWidth) = useState(512f)

    onDraw {
        fill(model.style[Style.AlternateBackgroundColour])
    }

    withPadding(32.px, 64.px).vdiv(48.px, spacing = 32.px) {
        withHorizontalAlignment(0.5f).dropdown(
            initialSelectedOption = width,
            options = listOf(512f, 768f, 1024f, 1516f),
            focused = true,
            onOptionChanged = setWidth,
            renderOption = { withPadding(12.px, 24.px).label("${it.toInt()}px", horizontalAlignment = 0f) },
            renderPrimaryOption = { withPadding(12.px, 24.px).label("${it.toInt()}px", horizontalAlignment = 0f) },
        )

        withPadding(32.px).withAlignment(0.5f, 0f).withWidth(width.px).withDecoration {
            withRegion(region.withPadding((-32).px)) {
                shadow(cornerRadius = 16.px)
                roundedRectangle(cornerRadius = 16.px, colour = model.style[Style.BackgroundColour])
            }
        } .flow(
            verticalSpacing = 16.px,
            horizontalSpacing = 16.px,
            verticalRowAlignment = 0f,
            horizontalRowAlignment = 0f,
        ) {
            for (i in 1 .. 1) {
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

fun main() {
    val window = GLFWWindowCreator.createWindow("UI")
    val controller = blobsController()

    controller.events.connect { window.draw { controller.repositionAndDraw(this) } }
    window.events.connect(controller::pushEvent)
    controller.load()

    while (!window.isClosed) GLFWWindowCreator.update()
}
