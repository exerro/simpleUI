package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal

@UndocumentedInternal
internal class PersistentComponentData(
    internal val id: Any?,
    internal val type: String,
    internal val hooks: HookManager = HookManager(),
)
