package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
@UIContextType
class DeferredComponentContext<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
        >(
    private val componentContext: ComponentContext<Model, Width, Height>,
): ComponentChildrenContext<Model, Width, Height>,
    ComponentContext<Model, Width, Height> by componentContext {
    override val ids = IdProvider(thisComponentId)
    override val model get() = componentContext.model

    override fun rawComponent(elementType: String, id: Id, init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved): ComponentIsResolved {
        return componentContext.children<Width, Height>({
            rawComponent(elementType, id, init)
        }) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
            val a = children.last()(width, height, availableWidth, availableHeight)
            a.copy(
                eventHandlers = eventHandlers + a.eventHandlers
            ) {
                for (f in drawFunctions) f(this)
                a.draw(this)
            }
        }
    }
}
