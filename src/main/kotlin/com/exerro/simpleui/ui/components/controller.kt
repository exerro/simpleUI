package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.filterIsInstance
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.LifecycleHook
import com.exerro.simpleui.ui.ParentDefinesMe
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.extensions.noChildren

@UndocumentedExperimentalUI
fun ComponentChildrenContext<*, ParentDefinesMe, ParentDefinesMe>.controller(
    controller: UIController<*>,
) = component("controller") {
    var loaded by useOrderedStorageCell { false }

    if (loaded) connectEventHandler(controller::pushEvent)
    if (loaded) onDraw(controller::draw)

    attachHook(LifecycleHook.Positioned { r ->
        if (loaded) controller.setSizeDirect(r.width, r.height)
        else {
            controller.load(r.width, r.height)
            loaded = true
        }
    })

    // run this once
    useOrderedStorageCell { controller.events
        .filterIsInstance<UIController.Event.Updated>()
        .connect { refresh() }
    }

    noChildren()
}
