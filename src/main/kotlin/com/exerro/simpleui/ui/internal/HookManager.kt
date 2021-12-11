package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.HookState

@UndocumentedInternal
internal class HookManager {
    private val hooks: MutableList<HookState> = mutableListOf()
    private var hookIndex = 0

    @UndocumentedInternal
    fun <H: HookState> getHookStateOrNew(createHook: () -> H): H {
        val existingHook = hooks.getOrNull(hookIndex)
        val hook = existingHook ?: createHook()

        if (existingHook == null) {
            hooks.add(hook)
        }

        ++hookIndex

        // this is totally unsafe but there's no other way :((
        @Suppress("UNCHECKED_CAST")
        return hook as H
    }

    @UndocumentedInternal
    fun reset() {
        hookIndex = 0
    }
}