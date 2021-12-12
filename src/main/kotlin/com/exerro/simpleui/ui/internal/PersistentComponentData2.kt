package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.ID

@UndocumentedInternal
internal class PersistentComponentData2(
    internal val id: ID,
    internal val elementType: String,
    internal val hooks: HookManager = HookManager(),
)
