package com.exerro.simpleui.layout

import com.exerro.simpleui.Undocumented

@Undocumented
fun DefinedLayoutContext.stack(
    init: DefinedLayoutContext.() -> Unit,
) = includeChild { allocatedWidth, allocatedHeight, availableWidth, availableHeight ->
    val children = mutableListOf<LayoutContext.Child<Nothing?, Nothing?>>()
    val context = object: DefinedLayoutContext {
        override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
            children.add(init(allocatedWidth, allocatedHeight, availableWidth, availableHeight))
        }
    }

    context.init()
    LayoutContext.Child(null, null) {
        children.forEach { it.draw(this) }
    }
}
