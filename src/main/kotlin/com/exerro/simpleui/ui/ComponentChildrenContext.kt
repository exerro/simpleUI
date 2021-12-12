package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
@UIContextType
interface ComponentChildrenContext<
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
    @UndocumentedExperimentalUI
    val ids: IdProvider

    @UndocumentedExperimentalUI
    fun rawComponent(
        elementType: String = "generic",
        id: Id = ids.localAnonymous(elementType),
        init: ComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentIsResolved
    ): ComponentIsResolved

    @UndocumentedExperimentalUI
    fun component(
        elementType: String = "generic",
        id: Id = ids.localAnonymous(elementType),
        init: DeferredComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.() -> ComponentIsResolved
    ) = rawComponent(elementType, id) { DeferredComponentContext(this).init() }
}
