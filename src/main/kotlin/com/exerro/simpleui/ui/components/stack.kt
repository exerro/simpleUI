package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.ui.ResolvedComponent
import com.exerro.simpleui.ui.ComponentChildrenContext
import com.exerro.simpleui.ui.UIModel
import com.exerro.simpleui.ui.internal.joinEventHandlers

@UndocumentedExperimental
fun <Model: UIModel> ComponentChildrenContext<Model, Float, Float, Nothing?, Nothing?>.stack(
    init: ComponentChildrenContext<Model, Float, Float, Nothing?, Nothing?>.() -> Unit
) = rawComponent("stack") {
    children(init) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val resolvedChildren = children.map { f -> f(width, height, availableWidth, availableHeight) }
        ResolvedComponent(null, null, joinEventHandlers(eventHandlers, resolvedChildren)) {
            for (f in drawFunctions) f(this)
            for (child in resolvedChildren) child.draw(this)
        }
    }
}
