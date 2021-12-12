package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.colour.RGBA
import com.exerro.simpleui.ui.ParentDefinedContext
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.stack
import com.exerro.simpleui.ui.components.vdiv
import com.exerro.simpleui.ui.components.vflow
import com.exerro.simpleui.ui.extensions.noChildren
import com.exerro.simpleui.ui.modifiers.*

fun ParentDefinedContext<*>.drawElement(fn: DrawContext.() -> Unit) = component {
    onDraw(fn)
    noChildren()
}

fun main() {
    val window = GLFWWindowCreator.createWindow("UI Layout")
    val sizes = listOf(16.px, 32.px, 64.px, 48.px, 24.px, 32.px)
    val widths = listOf(80, 100, 70, 60, 80, 50)
    val sidebarElements = (sizes zip widths).mapIndexed { i, (size, width) ->
        Triple(width.percent, size, if (i % 2 == 1) Colours.orange else Colours.blue)
    }

    val controller = UIController {
        onDraw {
            fill(Colours.charcoal)
        }

        hdiv(30.percent) {
            stack {
                drawElement { fill(Colours.black) }

                vdiv(64.px) {
                    this
                        .withPadding(vertical = 16.px, horizontal = 32.px)
                        .withHorizontalAlignment(0f)
                        .withWidth(50.percent)
                        .drawElement { roundedRectangle(cornerRadius = 4.px, colour = Colours.white) }

                    this
                        .withPadding(32.px)
                        .withVerticalAlignment(1f)
                        .vflow(spacing = 32.px, reversed = true) {
                            for ((width, height, colour) in sidebarElements) this
                                .withHorizontalAlignment(0.5f)
                                .withSize(width = width, height = height)
                                .drawElement {
                                    roundedRectangle(cornerRadius = 4.px, colour)
                                }
                        }
                }
            }

            vdiv(48.px) {
                withPadding(top = 16.px, left = 16.px, right = 16.px).hdiv(spacing = 16.px) {
                    for (i in 1..8) {
                        drawElement {
                            roundedRectangle(cornerRadius = 50.percent, colour = Colours.lightGrey)
                        }
                    }
                }

                stack {
                    this.
                        withAlignment(horizontalAlignment = 0f, verticalAlignment = 0f)
                        .withSize(width = 192.px, height = 128.px)
                        .withTranslation(dx = 128.px, dy = 96.px)
                        .drawElement {
                            shadow(cornerRadius = 8.px, colour = RGBA(14, 14, 14))
                            roundedRectangle(cornerRadius = 8.px, colour = Colours.red)
                        }

                    this.
                        withAlignment(horizontalAlignment = 0f, verticalAlignment = 0f)
                        .withSize(width = 128.px, height = 128.px)
                        .withTranslation(dx = 160.px, dy = 142.px)
                        .drawElement {
                            shadow(cornerRadius = 8.px, colour = RGBA(14, 14, 14))
                            roundedRectangle(cornerRadius = 8.px, colour = Colours.green)
                        }

                    this.
                        withAlignment(horizontalAlignment = 0f, verticalAlignment = 0f)
                        .withSize(width = 192.px, height = 128.px)
                        .withTranslation(dx = 256.px, dy = 164.px)
                        .drawElement {
                            shadow(cornerRadius = 8.px, colour = RGBA(14, 14, 14))
                            roundedRectangle(cornerRadius = 8.px, colour = Colours.blue)
                        }
                }
            }
        }
    }

    controller.events
        .connect { window.draw { controller.repositionAndDraw(this) } }

    window.events.connect(controller::pushEvent)

    controller.load()

    while (!window.isClosed) GLFWWindowCreator.update()
}
