package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal data class OnLoadHookState<T>(
    var evaluated: Boolean,
    var value: T?,
): HookState

@UndocumentedExperimentalUI
fun <T: Any> ComponentContext<*, *, *>.useOnce(fn: () -> T): T {
    val hook = getHookStateOrNew { OnLoadHookState<T>(false, null) }
    if (!hook.evaluated) {
        hook.evaluated = true
        hook.value = fn()
    }
    return hook.value!!
}
