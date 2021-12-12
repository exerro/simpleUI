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
inline fun <Model: UIModel, reified Height: WhoDefinesMe> ComponentChildrenContext<Model, ChildDefinesMe, Height>.hflow(
    spacing: Pixels = 0.px,
    reversed: Boolean = false,
    verticalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = false,
    noinline init: ComponentChildrenContext<Model, ChildDefinesMe, Height>.() -> Unit
) = component("hflow") {
    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

    children(init) { _, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(availableWidth) + separatorThickness
        val sizeResolvedChildren = (if (reversed) children.reversed() else children).map { child ->
            child(nothingForChild(), height, availableWidth, availableHeight)
        }
        val childHeight = if (sizeResolvedChildren.isNotEmpty()) sizeResolvedChildren.maxOf { fixFromChildAny(it.height) } else 0f
        val sumWidth = sizeResolvedChildren.fold(0f) { a, b -> a + fixFromChild(b.width) }
        val totalWidth = sumWidth + spacingValue * (children.size - 1)

        SizeResolvedComponent(fixForParent(totalWidth), fixForParentAny(childHeight)) { r ->
            var lastX = 0f
            val positionResolvedChildren = sizeResolvedChildren.map { c ->
                val thisX = lastX

                lastX += round(fixFromChild(c.width) + spacingValue)

                c.positionResolver(r
                    .resizeTo(width = fixFromChild(c.width).px, height = (fixFromChildAnyOptional(c.height) ?: r.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = r.x + thisX))
            }

            PositionResolvedComponent(r, joinEventHandlers(eventHandlers, positionResolvedChildren)) {
                var separatorX = 0f
                for (f in drawFunctions) f(this)

                for ((i, child) in positionResolvedChildren.withIndex()) {
                    if (showSeparators && i > 0)
                        withRegion(r.copy(
                            x = r.x + separatorX + floor(-spacingValue + (spacingValue - separatorThickness) / 2) - 1,
                            height = separatorThickness
                        )) { fill(separatorColour) }

                    withRegion(child.region, draw = child.draw)

                    separatorX += round(fixFromChild(sizeResolvedChildren[i].width) + spacingValue)
                }
            }
        }
    }
}
