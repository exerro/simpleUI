package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.filterIsInstance
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.ParentDefinesMe
import com.exerro.simpleui.ui.UIController
import com.exerro.simpleui.ui.hooks.useOnce
import com.exerro.simpleui.ui.noChildren

@UndocumentedExperimentalUI
fun ComponentChildrenContext<*, ParentDefinesMe, ParentDefinesMe>.controller(
    controller: UIController<*>,
) = rawComponent("controller") {
    useOnce { controller.load() }
    connectEventHandler(controller::pushEvent)
    onDraw(controller::draw)

    useOnce { controller.events
        .filterIsInstance<UIController.Event.Refreshed>()
        .connect { refresh() } }

    noChildren()
}
