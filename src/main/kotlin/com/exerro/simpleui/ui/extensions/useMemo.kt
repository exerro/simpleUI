package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.ComponentContext

@UndocumentedExperimentalUI
fun <T, R> ComponentContext<*, *, *>.useMemo(
    key: T,
    single: Boolean = false,
    fn: (T) -> R,
): R {
    val map by useOrderedStorageCell { mutableMapOf<T, R>() }
    if (key in map) return map[key]!!
    if (single) map.clear()
    val result = fn(key)
    map[key] = result
    return result
}
