package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class StateHookState<T>(
    var value: T,
    var lastInitialValue: T,
): HookState

@UndocumentedExperimental
fun <T> ComponentContext<*, *, *, *, *>.useState(
    initialValue: T,
    handleVaryingInitialValue: Boolean = false,
): Triple<T, (T) -> Unit, Boolean> {
    val hook = getHookStateOrRegister { StateHookState(initialValue, initialValue) }
    var changed = false

    if (handleVaryingInitialValue && hook.lastInitialValue != initialValue) {
        hook.lastInitialValue = initialValue
        hook.value = initialValue
        changed = true
    }

    return Triple(hook.value, {
        hook.value = it
        refresh()
    }, changed)
}
