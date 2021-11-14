package com.exerro.simpleui.layout

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
fun DrawContext.layout(init: DefinedLayoutContext.() -> Unit) {
    val region = region
    val context = object: DefinedLayoutContext {
        override fun includeChild(init: (Float, Float, Float, Float) -> LayoutContext.Child<Nothing?, Nothing?>) {
            val c = init(region.width, region.height, region.width, region.height)
            c.draw(this@layout)
        }
    }
    context.init()
}
