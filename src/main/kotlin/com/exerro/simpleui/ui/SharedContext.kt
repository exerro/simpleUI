package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
interface SharedContext<Model: UIModel> {
    @UndocumentedExperimentalUI
    val model: Model

    @UndocumentedExperimentalUI
    fun setModel(model: Model)

    @UndocumentedExperimentalUI
    fun updateModel(update: (Model) -> Model) =
        setModel(update(model))
}
