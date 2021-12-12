package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
interface UIModel {
    @UndocumentedExperimentalUI
    val style: Style

    @UndocumentedExperimentalUI
    val keybinds: ActionKeybinds

    companion object: UIModel {
        override val style = Style.Dark
        override val keybinds = ActionKeybinds.Default

        @UndocumentedExperimentalUI
        operator fun invoke(
            style: Style = Style.Dark,
            keybinds: ActionKeybinds = ActionKeybinds.Default
        ) = object: UIModel {
            override val style = style
            override val keybinds = keybinds
        }
    }
}
