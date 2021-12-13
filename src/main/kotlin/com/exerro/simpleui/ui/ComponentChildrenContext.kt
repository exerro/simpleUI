package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
@UIContextType
interface ComponentChildrenContext<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
>: SharedContext<Model> {
    @UndocumentedExperimentalUI
    val ids: IdProvider

    @UndocumentedExperimentalUI
    fun component(
        elementType: String = "generic",
        id: Id = ids.localAnonymous(elementType),
        init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved
    ): ComponentIsResolved
}
