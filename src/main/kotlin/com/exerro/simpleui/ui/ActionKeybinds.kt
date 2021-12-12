package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.standardActions.*

@UndocumentedExperimentalUI
interface ActionKeybinds {
    @UndocumentedExperimentalUI
    operator fun get(action: Action): List<ActionKeybind>

    @UndocumentedExperimentalUI
    operator fun get(keybind: ActionKeybind): List<Action>

    companion object {
        @UndocumentedExperimentalUI
        fun create(vararg binds: Pair<ActionKeybind, Action>) = object: ActionKeybinds {
            val actions = binds.groupBy { it.second } .mapValues { (_, v) -> v.map { it.first } }
            val keybinds = binds.groupBy { it.first } .mapValues { (_, v) -> v.map { it.second } }

            override fun get(action: Action) = actions[action] ?: emptyList()
            override fun get(keybind: ActionKeybind) = keybinds[keybind] ?: emptyList()
        }

        @UndocumentedExperimentalUI
        fun combine(vararg keybinds: ActionKeybinds) = object: ActionKeybinds {
            override fun get(action: Action) = keybinds.flatMap { it[action] }
            override fun get(keybind: ActionKeybind) = keybinds.flatMap { it[keybind] }
        }

        @UndocumentedExperimentalUI
        val Default = create(
            ActionKeybind("tab") to FocusNextElement,
            ActionKeybind("shift+tab") to FocusPreviousElement,
            ActionKeybind("left") to MoveFocusLeft,
            ActionKeybind("right") to MoveFocusRight,
            ActionKeybind("up") to MoveFocusUp,
            ActionKeybind("down") to MoveFocusDown,
            ActionKeybind("enter") to Confirm,
            ActionKeybind("enter") to SelectEntity,
        )
    }
}
