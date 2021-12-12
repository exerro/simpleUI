package com.exerro.simpleui

import com.exerro.simpleui.ui.Style
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.controller
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.panel
import com.exerro.simpleui.ui.get
import com.exerro.simpleui.ui.hooks.useOnce
import com.exerro.simpleui.ui.modifiers.withPadding

fun main() = UIController.runDefaultApp("UI") {
    val blobs = useOnce { blobsController() }

    onDraw {
        fill(model.style[Style.AlternateBackgroundColour])
    }

    withPadding(32.px, 64.px).hdiv(30.percent, spacing = 32.px) {
        panel(model.style[Style.BackgroundColour])
        controller(blobs)
    }
}
