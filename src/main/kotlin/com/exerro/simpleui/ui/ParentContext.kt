package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
typealias ParentDefinedContext = ParentContext<Float, Float, Nothing?, Nothing?>

@UndocumentedExperimental
typealias ComponentDefinedContext = ParentContext<Nothing?, Nothing?, Float, Float>

@UndocumentedExperimental
@UIContextType
interface ParentContext<
        /** Width provided by parent to children. */
        ParentWidth: Float?,
        /** Height provided by parent to children. */
        ParentHeight: Float?,
        /** Width provided by children to parent. */
        ChildWidth: Float?,
        /** Height provided by children to parent. */
        ChildHeight: Float?,
> {
    // val style: Style

    @UndocumentedExperimental
    fun rawComponent(
        elementType: String = "generic",
        trackingId: Any? = null,
        init: ComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ChildReturn
    ): ChildReturn

    @UndocumentedExperimental
    fun component(
        elementType: String = "generic",
        trackingId: Any? = null,
        init: BasicComponentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ChildReturn
    ) = rawComponent(elementType, trackingId) { BasicComponentContext(this).init() }
}
