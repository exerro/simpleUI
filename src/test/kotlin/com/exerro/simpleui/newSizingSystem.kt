package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.panel
import com.exerro.simpleui.ui.modifiers.withHeight
import com.exerro.simpleui.ui.modifiers.withVerticalAlignment

fun main() = UIController.runDefaultApp("New Sizing System") {
    withVerticalAlignment(0.5f).hdiv(30.percent, verticalAlignment = 1f) {
        withHeight(128.px).panel(Colours.charcoal)
        withHeight(256.px).panel(Colours.teal)
    }
}
