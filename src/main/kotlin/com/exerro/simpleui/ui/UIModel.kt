package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
interface UIModel {
    @UndocumentedExperimental
    val style: Style

    @UndocumentedExperimental
    val keybinds: ActionKeybinds

    companion object: UIModel {
        override val style = Style.Dark
        override val keybinds = ActionKeybinds.Default

        @UndocumentedExperimental
        operator fun invoke(style: Style, keybinds: ActionKeybinds) = object: UIModel {
            override val style = style
            override val keybinds = keybinds
        }
    }
}
