package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.joinEventHandlers
import kotlin.math.floor
import kotlin.math.round

@UndocumentedExperimental
fun <Model: UIModel, ParentHeight: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, Nothing?, ParentHeight, Float, ChildHeight>.hflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    verticalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = false,
    init: ComponentChildrenContext<Model, Nothing?, ParentHeight, Float, ChildHeight>.() -> Unit
) = rawComponent("hflow") {
    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

    children(init) { _, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(availableWidth) + separatorThickness
        val appliedChildren = (if (reversed) children.reversed() else children).map { child ->
            child(null, height, availableWidth, availableHeight)
        }
        val childHeight = (if (height == null) if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { it.height as Float } else 0f else null) as ChildHeight
        val sumWidth = appliedChildren.fold(0f) { a, b -> a + b.width }
        val totalWidth = sumWidth + spacingValue * (children.size - 1)

        ResolvedComponent(totalWidth, childHeight, joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastX = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                if (showSeparators && i > 0)
                    region.copy(
                        x = region.x + lastX - floor((spacingValue + separatorThickness) / 2),
                        width = separatorThickness
                    ).draw { fill(separatorColour) }

                region
                    .resizeTo(width = c.width.px, height = (c.height ?: region.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = region.x + lastX)
                    .draw(draw = c.draw)

                lastX += round(c.width + spacingValue)
            }
        }
    }
}
