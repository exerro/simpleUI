package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
@UIContextType
class BasicComponentContext<
        /** Width provided by parent to this component. */
        ParentWidth: Float?,
        /** Height provided by parent to this component. */
        ParentHeight: Float?,
        /** Width provided by this component to parent. */
        ChildWidth: Float?,
        /** Height provided by this component to parent. */
        ChildHeight: Float?,
        >(
    private val componentContext: ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>,
): ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>,
    ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight> by componentContext {
    override fun rawComponent(elementType: String, trackingId: Any?, init: ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ChildReturn): ChildReturn {
        return componentContext.children<ParentWidth, ParentHeight, ChildWidth, ChildHeight>({
            rawComponent(elementType, trackingId, init)
        }) { width, height, availableWidth, availableHeight, drawFunctions, children ->
            val a = children[0](width, height, availableWidth, availableHeight)
            a.copy {
                for (f in drawFunctions) f(this)
                a.draw(this)
            }
        }
    }
}
