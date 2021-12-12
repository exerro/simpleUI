package com.exerro.simpleui

import com.exerro.simpleui.ui.Style
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.button
import com.exerro.simpleui.ui.components.vflow
import com.exerro.simpleui.ui.get
import com.exerro.simpleui.ui.modifiers.withHorizontalAlignment
import com.exerro.simpleui.ui.modifiers.withPadding
import com.exerro.simpleui.ui.modifiers.withVerticalAlignment

fun main() = UIController.runDefaultApp {
    onDraw {
        fill(model.style[Style.BackgroundColour])
    }

    withPadding(32.px).withVerticalAlignment(0f).vflow(showSeparators = false, spacing = 0.px) {
        withPadding(16.px).button("Button 1")
        withHorizontalAlignment(0.5f).button("Button 2")
    }
}
