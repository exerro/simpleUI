package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
@UIContextType
class DeferredComponentContext<
        Model: UIModel,
        /** Width provided by parent to this component. */
        ParentWidth: Float?,
        /** Height provided by parent to this component. */
        ParentHeight: Float?,
        /** Width provided by this component to parent. */
        ChildWidth: Float?,
        /** Height provided by this component to parent. */
        ChildHeight: Float?,
        >(
    private val componentContext: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>,
): ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>,
    ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight> by componentContext {
    override val model get() = componentContext.model

    override fun rawComponent(elementType: String, trackingId: Any?, init: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentReturn): ComponentReturn {
        return componentContext.children<ParentWidth, ParentHeight, ChildWidth, ChildHeight>({
            rawComponent(elementType, trackingId, init)
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
