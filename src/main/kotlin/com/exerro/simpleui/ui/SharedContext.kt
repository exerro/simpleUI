package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
interface SharedContext<Model: UIModel> {
    @UndocumentedExperimental
    val model: Model

    @UndocumentedExperimental
    fun setModel(model: Model)

    @UndocumentedExperimental
    fun updateModel(update: (Model) -> Model) =
        setModel(update(model))
}
