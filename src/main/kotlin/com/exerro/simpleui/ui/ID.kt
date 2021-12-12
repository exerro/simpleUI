package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
sealed interface ID {
    @UndocumentedExperimentalUI
    object Root: ID

    @UndocumentedExperimentalUI
    data class Global(val id: String): ID

    @UndocumentedExperimentalUI
    data class LocalNamed(val id: String, val parent: ID): ID

    @UndocumentedExperimentalUI
    data class LocalAnonymous(val index: Int, val elementType: String, val parent: ID): ID
}
