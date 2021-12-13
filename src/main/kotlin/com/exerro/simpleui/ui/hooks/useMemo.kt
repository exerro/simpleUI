package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal data class MemoHookState<T, R>(
    val map: MutableMap<T, R> = mutableMapOf(),
): HookState

@UndocumentedExperimentalUI
fun <T, R> ComponentContext<*, *, *>.useMemo(
    key: T,
    single: Boolean = false,
    fn: (T) -> R,
): R {
    val hook = getHookStateOrNew { MemoHookState<T, R>() }
    if (key in hook.map) return hook.map[key]!!
    if (single) hook.map.clear()
    val result = fn(key)
    hook.map[key] = result
    return result
}
