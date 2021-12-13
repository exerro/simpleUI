package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.EKeyPressed
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.Action
import com.exerro.simpleui.ui.ActionKeybind
import com.exerro.simpleui.ui.ComponentContext

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *>.bind(keybind: ActionKeybind, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val handled = keybind.keyName == event.name && keybind.modifiers == event.modifiers && (keybind.allowRepeats || !event.isRepeat)

    handled && behaviour()
}

@UndocumentedExperimentalUI
fun ComponentContext<*, *, *>.bind(action: Action, behaviour: () -> Boolean) = connectEventHandler { event ->
    if (event !is EKeyPressed) return@connectEventHandler false
    val keybinds = model.keybinds[action]
    val handled = keybinds.any {
        it.keyName == event.name && it.modifiers == event.modifiers && (it.allowRepeats || !event.isRepeat)
    }

    handled && behaviour()
}
