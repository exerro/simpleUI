package com.exerro.simpleui.layout

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.Undocumented

@Undocumented
fun DrawContext.layout(init: DefinedLayoutContext.() -> Unit) {
    val context = object: DefinedLayoutContext {
        override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
            val c = init(this@layout.region.width, this@layout.region.height, this@layout.region.width, this@layout.region.height)
            c.draw(this@layout)
        }
    }
    context.init()
}
