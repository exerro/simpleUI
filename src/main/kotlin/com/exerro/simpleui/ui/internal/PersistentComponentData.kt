package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.ui.Id
import com.exerro.simpleui.ui.LifecycleHook

@UndocumentedInternal
internal class PersistentComponentData(
    internal val id: Id,
    internal val elementType: String,
    internal val storage: PersistentStorageManager = PersistentStorageManager(),
    internal var refCount: Int = 0,
    internal var isMounted: Boolean = false,
    internal var childIds: Set<Id> = emptySet(),
    internal var lifecycleHooks: List<LifecycleHook> = emptyList(),
)
