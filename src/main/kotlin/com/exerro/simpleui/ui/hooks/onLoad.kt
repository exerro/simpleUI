package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class OnLoadHookState<T>(
    var evaluated: Boolean,
    var value: T?,
): HookState

@UndocumentedExperimental
fun <T> ComponentContext<*, *, *, *, *>.onLoad(fn: () -> T): T {
    val hook = getHookStateOrRegister { OnLoadHookState<T>(false, null) }
    if (!hook.evaluated) {
        hook.evaluated = true
        hook.value = fn()
    }
    return hook.value!!
}
