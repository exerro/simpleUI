package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.ComponentContext

@UndocumentedExperimentalUI
fun <T> ComponentContext<*, *, *>.useState(
    initialValue: T,
    updateOnVaryingInitialValue: Boolean = false,
): Triple<T, (T) -> Unit, Boolean> {
    var value by useOrderedStorageCell { initialValue }
    var lastValue by useOrderedStorageCell { initialValue }
    var changed = false

    if (updateOnVaryingInitialValue && lastValue != initialValue) {
        lastValue = initialValue
        value = initialValue
        changed = true
    }

    return Triple(value, {
        value = it
        refresh()
    }, changed)
}
