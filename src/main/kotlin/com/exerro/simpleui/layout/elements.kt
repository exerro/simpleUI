package com.exerro.simpleui.layout

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.Undocumented

@Undocumented
fun DefinedLayoutContext.drawElement(
    draw: DrawContext.() -> Unit
) {
    includeChild { _, _, _, _ -> LayoutContext.Child(null, null, draw) }
}