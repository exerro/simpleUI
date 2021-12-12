package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.calculateInverse
import com.exerro.simpleui.ui.internal.joinEventHandlers
import kotlin.math.floor
import kotlin.math.round

@UndocumentedExperimental
fun <Model: UIModel, ParentWidth: Float?, ChildWidth: Float?> ComponentChildrenContext<Model, ParentWidth, Nothing?, ChildWidth, Float>.vflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    horizontalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = false,
    init: ComponentChildrenContext<Model, ParentWidth, Nothing?, ChildWidth, Float>.() -> Unit
) = rawComponent("vflow") {
    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

    children(init) { width, _, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(availableHeight) + separatorThickness
        val appliedChildren = (if (reversed) children.reversed() else children).map { child ->
            child(width, null, availableWidth, availableHeight)
        }
        val childWidth = calculateInverse<ChildWidth>(width) { if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { it.width as Float } else 0f }
        val sumHeight = appliedChildren.fold(0f) { a, b -> a + b.height }
        val totalHeight = sumHeight + spacingValue * (children.size - 1)

        ResolvedComponent(childWidth, totalHeight, joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastY = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                if (showSeparators && i > 0)
                    region.copy(
                        y = region.y + lastY - floor((spacingValue + separatorThickness) / 2),
                        height = separatorThickness
                    ).draw { fill(separatorColour) }

                region
                    .resizeTo(height = c.height.px, width = (c.width ?: region.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = region.y + lastY)
                    .draw(draw = c.draw)

                lastY += round(c.height + spacingValue)
            }
        }
    }
}
