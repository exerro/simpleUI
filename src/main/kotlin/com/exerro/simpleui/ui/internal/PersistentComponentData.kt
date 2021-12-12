package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.Id

@UndocumentedInternal
internal class PersistentComponentData(
    internal val id: Id,
    internal val elementType: String,
    internal val hooks: HookManager = HookManager(),
)
