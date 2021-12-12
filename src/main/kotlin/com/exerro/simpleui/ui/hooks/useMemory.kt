package com.exerro.simpleui.ui.hooks

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ComponentContext
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class MemoryHookState<T>(
    var value: T? = null,
): HookState

@UndocumentedExperimentalUI
fun <T> ComponentContext<*, *, *, *, *>.useMemory(): Pair<() -> T?, (T) -> Unit> {
    val hook = getHookStateOrRegister { MemoryHookState<T>() }
    return Pair({ hook.value }, { hook.value = it })
}
