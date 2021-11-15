package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
@UIContextType
interface ParentContext<
        Model: UIModel,
        /** Width provided by parent to children. */
        ParentWidth: Float?,
        /** Height provided by parent to children. */
        ParentHeight: Float?,
        /** Width provided by children to parent. */
        ChildWidth: Float?,
        /** Height provided by children to parent. */
        ChildHeight: Float?,
>: SharedContext<Model> {
    @UndocumentedExperimental
    @BuilderInference
    fun rawComponent(
        elementType: String = "generic",
        trackingId: Any? = null,
        init: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentReturn
    ): ComponentReturn

    @UndocumentedExperimental
    @BuilderInference
    fun component(
        elementType: String = "generic",
        trackingId: Any? = null,
        init: BasicComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentReturn
    ) = rawComponent(elementType, trackingId) { BasicComponentContext(this).init() }
}
