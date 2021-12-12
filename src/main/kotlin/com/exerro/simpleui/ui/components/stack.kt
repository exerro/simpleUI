package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.joinEventHandlers

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.stack(
    init: ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.() -> Unit
) = rawComponent("stack") {
    children(init) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val resolvedChildren = children.map { f -> f(width, height, availableWidth, availableHeight) }
        SizeResolvedComponent(nothingForParent(), nothingForParent(), joinEventHandlers(eventHandlers, resolvedChildren)) {
            for (f in drawFunctions) f(this)
            for (child in resolvedChildren) child.draw(this)
        }
    }
}
