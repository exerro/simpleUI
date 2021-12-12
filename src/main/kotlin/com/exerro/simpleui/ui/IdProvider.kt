package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
class IdProvider(val parent: Id) {
    @UndocumentedExperimentalUI
    fun global(id: String) = Id.Global(id)

    @UndocumentedExperimentalUI
    fun localNamed(id: String) = Id.LocalNamed(id, parent)

    @UndocumentedExperimentalUI
    fun localAnonymous(elementType: String) =
        Id.LocalAnonymous(counter.compute(elementType) { _, v -> v?.let { it + 1 } ?: 0 }!!, elementType, parent)

    private val counter = mutableMapOf<String, Int>()
}
