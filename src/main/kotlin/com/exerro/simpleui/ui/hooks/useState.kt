package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class StateHookState<T>(
    var value: T,
    var lastInitialValue: T,
): HookState

@UndocumentedExperimentalUI
fun <T> ComponentContext<*, *, *>.useState(
    initialValue: T,
    updateOnVaryingInitialValue: Boolean = false,
): Triple<T, (T) -> Unit, Boolean> {
    val hook = getHookStateOrNew { StateHookState(initialValue, initialValue) }
    var changed = false

    if (updateOnVaryingInitialValue && hook.lastInitialValue != initialValue) {
        hook.lastInitialValue = initialValue
        hook.value = initialValue
        changed = true
    }

    return Triple(hook.value, {
        hook.value = it
        refresh()
    }, changed)
}
