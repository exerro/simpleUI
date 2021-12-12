package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.joinEventHandlers

@UndocumentedExperimentalUI
fun <Model: UIModel> ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.stack(
    init: ComponentChildrenContext<Model, ParentDefinesMe, ParentDefinesMe>.() -> Unit
) = component("stack") {
    children(init) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val sizeResolvedChildren = children.map { f -> f(width, height, availableWidth, availableHeight) }
        ResolvedComponentSizePhase(nothingForParent(), nothingForParent()) { r ->
            val positionResolvedChildren = sizeResolvedChildren.map { it.positionResolver(r) }
            ResolvedComponentPositionPhase(r, joinEventHandlers(eventHandlers, positionResolvedChildren)) {
                for (f in drawFunctions) f(this)
                for (child in positionResolvedChildren) child.draw(this)
            }
        }
    }
}
