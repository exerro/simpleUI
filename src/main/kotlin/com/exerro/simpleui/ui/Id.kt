package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
sealed interface Id {
    @UndocumentedExperimentalUI
    object Root: Id

    @UndocumentedExperimentalUI
    data class Global(val id: String): Id

    @UndocumentedExperimentalUI
    data class LocalNamed(val id: String, val parent: Id): Id

    @UndocumentedExperimentalUI
    data class LocalAnonymous(val index: Int, val elementType: String, val parent: Id): Id
}
