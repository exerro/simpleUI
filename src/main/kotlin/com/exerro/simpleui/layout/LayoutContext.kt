package com.exerro.simpleui.layout

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.DrawContextDSL
import com.exerro.simpleui.UndocumentedExperimental

@DslMarker
annotation class LayoutBuilder

@UndocumentedExperimental
typealias DefinedLayoutContext = LayoutContext<Float, Float, Nothing?, Nothing?>

@UndocumentedExperimental
typealias UndefinedLayoutContext = LayoutContext<Nothing?, Nothing?, Float, Float>

@UndocumentedExperimental
@LayoutBuilder
@DrawContextDSL
interface LayoutContext<
        out Width: Float?,
        out Height: Float?,
        in ChildWidth: Float?,
        in ChildHeight: Float?,
> {
    @UndocumentedExperimental
    fun includeChild(
        init: (
            allocatedWidth: Width,
            allocatedHeight: Height,
            availableWidth: Float,
            availableHeight: Float,
        ) -> Child<ChildWidth, ChildHeight>,
    )

    @UndocumentedExperimental
    data class Child<out Width: Float?, out Height: Float?>(
        val width: Width,
        val height: Height,
        val draw: DrawContext.() -> Unit
    )
}
