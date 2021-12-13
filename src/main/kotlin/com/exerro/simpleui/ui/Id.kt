package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
sealed interface Id {
    @UndocumentedExperimentalUI
    object Root: Id {
        override fun toString() = "<root>"
    }

    @UndocumentedExperimentalUI
    data class Global(val id: String): Id {
        override fun toString() = id
    }

    @UndocumentedExperimentalUI
    data class LocalNamed(val id: String, val parent: Id): Id {
        override fun toString() = "$parent.$id"
    }

    @UndocumentedExperimentalUI
    data class LocalAnonymous(val index: Int, val elementType: String, val parent: Id): Id {
        override fun toString() = "$parent:$elementType[$index]"
    }
}
