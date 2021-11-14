package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.panel
import com.exerro.simpleui.ui.components.stack
import com.exerro.simpleui.ui.components.vdiv
import com.exerro.simpleui.ui.hooks.onLoad
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.tracked
import com.exerro.simpleui.ui.modifiers.withAlignment
import com.exerro.simpleui.ui.modifiers.withPadding
import com.exerro.simpleui.ui.modifiers.withSize
import com.exerro.simpleui.ui.noChildren
import withTranslation
import kotlin.concurrent.thread

fun main() {
    val window = GLFWWindowCreator.createWindow("HelloWorld")

    lateinit var changeOuterState: () -> Unit
    lateinit var changeInnerState: () -> Unit

    val component = UIController {
        val (count, setCount) = useState(0)

        onLoad {
            println("Loaded outer")
        }

        onDraw {
            fill(Colours.red)
        }

        changeOuterState = { setCount(count + 1) }

        vdiv(128.px) {
            withPadding(16.px).stack {
                panel(Colours.charcoal)

                rawComponent {
                    onDraw { write("Hello $count", Colours.teal) }
                    noChildren()
                }
            }

            hdiv {
                withPadding(16.px).rawComponent {
                    val (innerCount, setInnerCount) = useState(count)

                    onLoad {
                        println("Loaded inner")
                    }

                    changeInnerState = { setInnerCount(innerCount + 1) }

                    onDraw {
                        fill(Colours.blue)
                        write("count: $count, innerCount: $innerCount", Colours.white)

                        if (innerCount >= 3) {
                            region.resizeTo(height = 32.px).below().draw { write("${Math.random()}", Colours.white) }
                            dynamicContent()
                        }
                    }

                    noChildren()
                }

                for (i in 0 until count) {
                    rawComponent(trackingId = "sub$i") {
                        val colour = onLoad {
                            println("Loaded #$i")
                            Colours.random()
                        }

                        onDraw {
                            fill(colour)
                        }

                        noChildren()
                    }
                }
            }
        }
    }

    component.events.connect {
        println("Refreshed")
        window.draw { component.draw(this) }
        Thread.sleep(1000)
    }

    component.load()

    thread(start = true) {
        println(": changeInnerState()")
        changeInnerState()
        println(": changeInnerState()")
        changeInnerState()
        println(": changeOuterState()")
        changeOuterState()
        println(": changeOuterState()")
        changeOuterState()
        println(": changeOuterState()")
        changeOuterState()
        println(": changeInnerState()")
        changeInnerState()
        println(": changeOuterState()")
        changeOuterState()
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
