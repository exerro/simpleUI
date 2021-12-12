package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.joinEventHandlers
import kotlin.math.floor
import kotlin.math.round

@UndocumentedExperimentalUI
inline fun <Model: UIModel, reified Width: WhoDefinesMe> ComponentChildrenContext<Model, Width, ChildDefinesMe>.vflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    horizontalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = false,
    noinline init: ComponentChildrenContext<Model, Width, ChildDefinesMe>.() -> Unit
) = component("vflow") {
    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

    children(init) { width, _, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(availableHeight) + separatorThickness
        val sizeResolvedChildren = (if (reversed) children.reversed() else children).map { child ->
            child(width, nothingForChild(), availableWidth, availableHeight)
        }
        val childWidth = if (sizeResolvedChildren.isNotEmpty()) sizeResolvedChildren.maxOf { fixFromChildAny(it.width) } else 0f
        val sumHeight = sizeResolvedChildren.fold(0f) { a, b -> a + fixFromChild(b.height) }
        val totalHeight = sumHeight + spacingValue * (children.size - 1)

        SizeResolvedComponent(fixForParentAny(childWidth), fixForParent(totalHeight)) { r ->
            var lastY = 0f
            val positionResolvedChildren = sizeResolvedChildren.map { c ->
                val thisY = lastY

                lastY += round(fixFromChild(c.height) + spacingValue)

                c.positionResolver(r
                    .resizeTo(height = fixFromChild(c.height).px, width = (fixFromChildAnyOptional(c.width) ?: r.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = r.y + thisY))
            }

            PositionResolvedComponent(r, joinEventHandlers(eventHandlers, positionResolvedChildren)) {
                var separatorY = 0f
                for (f in drawFunctions) f(this)

                for ((i, child) in positionResolvedChildren.withIndex()) {
                    if (showSeparators && i > 0)
                        withRegion(r.copy(
                            y = r.y + separatorY + floor(-spacingValue + (spacingValue - separatorThickness) / 2),
                            height = separatorThickness
                        )) { fill(separatorColour) }

                    withRegion(child.region, draw = child.draw)

                    separatorY += round(fixFromChild(sizeResolvedChildren[i].height) + spacingValue)
                }
            }
        }
    }
}
