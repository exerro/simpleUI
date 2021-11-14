package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class StateHookState<T>(
    var value: T,
): HookState

@UndocumentedExperimental
fun <T> ComponentContext<*, *, *, *>.useState(initialValue: T): Pair<T, (T) -> Unit> {
    val hook = getHookStateOrRegister { StateHookState(initialValue) }
    return hook.value to {
        hook.value = it
        refresh()
    }
}
