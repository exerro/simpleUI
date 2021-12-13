package com.exerro.simpleui

import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.components.hdiv
import com.exerro.simpleui.ui.components.panel
import com.exerro.simpleui.ui.components.stack
import com.exerro.simpleui.ui.extensions.bind
import com.exerro.simpleui.ui.extensions.singleChild
import com.exerro.simpleui.ui.extensions.useState
import com.exerro.simpleui.ui.standardActions.MoveFocusDown
import com.exerro.simpleui.ui.standardActions.MoveFocusLeft
import com.exerro.simpleui.ui.standardActions.MoveFocusRight

fun ComponentChildrenContext<*, ParentDefinesMe, ParentDefinesMe>.yeet(id: Id, a: String) = component(id = id) {
    attachHook(LifecycleHook.LoadHook {
        println("I was loaded $a")
    })

    attachHook(LifecycleHook.UnloadHook {
        println("I was unloaded $a")
    })

    singleChild.panel(Colours.teal)
}

fun main() = UIController.runDefaultApp { window ->
    val start by useOrderedStorageCell { System.currentTimeMillis() }
    val (isVisibleL, setVisibleL) = useState(true)
    val (isVisibleR, setVisibleR) = useState(true)
    val id = ids.localNamed("child")

    bind(MoveFocusLeft) {
        setVisibleL(!isVisibleL)
        true
    }

    bind(MoveFocusRight) {
        setVisibleR(!isVisibleR)
        true
    }

    bind(MoveFocusDown) {
        refresh()
        true
    }

    onDraw {
        fill(model.style[Style.BackgroundColour])
        println("Drawing ${System.currentTimeMillis() - start}")
    }

    singleChild.hdiv {
        panel(Colours.charcoal)

        stack {
            panel(Colours.red)

            if (!isVisibleL) yeet(id, "L")
        }

        if (isVisibleR) yeet(id, "R")
    }
}
