package com.exerro.simpleui

import com.exerro.simpleui.ui.Style
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.components.animatedRegion
import com.exerro.simpleui.ui.components.button
import com.exerro.simpleui.ui.components.vflow
import com.exerro.simpleui.ui.extensions.bind
import com.exerro.simpleui.ui.get
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.withPadding
import com.exerro.simpleui.ui.modifiers.withVerticalAlignment
import com.exerro.simpleui.ui.standardActions.SelectEntity

fun main() = UIController.runDefaultApp {
    val (toggled, setToggled) = useState(false)

    onDraw {
        fill(model.style[Style.BackgroundColour])
    }

    bind(SelectEntity) {
        setToggled(!toggled)
        true
    }

    withVerticalAlignment(0f).vflow(showSeparators = false, spacing = 0.px) {
        withPadding(top = (if (toggled) 256.px else 16.px)).animatedRegion {
            button("Button 1")
        }
    }
}
