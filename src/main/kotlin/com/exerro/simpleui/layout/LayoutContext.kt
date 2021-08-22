package com.exerro.simpleui.layout

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.DrawContextDSL
import com.exerro.simpleui.Undocumented

@DslMarker
annotation class LayoutBuilder

@Undocumented
typealias DefinedLayoutContext = LayoutContext<Float, Float, Nothing?, Nothing?>

@Undocumented
typealias UndefinedLayoutContext = LayoutContext<Nothing?, Nothing?, Float, Float>

@Undocumented
@LayoutBuilder
@DrawContextDSL
interface LayoutContext<
        Width: Float?,
        Height: Float?,
        ChildWidth: Float?,
        ChildHeight: Float?,
> {
    @Undocumented
    fun includeChild(
        init: (
            allocatedWidth: Width,
            allocatedHeight: Height,
            availableWidth: Float,
            availableHeight: Float,
        ) -> Child<ChildWidth, ChildHeight>,
    )

    @Undocumented
    data class Child<Width: Float?, Height: Float?>(
        val width: Width,
        val height: Height,
        val draw: DrawContext.() -> Unit
    )
}
