package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.Style
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.textInput
import com.exerro.simpleui.ui.extensions.singleChild
import com.exerro.simpleui.ui.get
import com.exerro.simpleui.ui.hooks.useOnce
import com.exerro.simpleui.ui.modifiers.withAlignment

fun main() = UIController.runDefaultApp { window ->
    val start = useOnce { System.currentTimeMillis() }

    onDraw {
        fill(model.style[Style.BackgroundColour])
        println("Drawing ${System.currentTimeMillis() - start}")
    }

    singleChild.withAlignment(0.5f, 0.5f).textInput(TextBufferBuilder {
        emitCursor(Colours.white, window.createdAt)
        emitText("Hello world", Colours.white)
    })
}
