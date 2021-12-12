package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.internal.joinEventHandlers
import com.exerro.simpleui.ui.internal.resolveFlowChildSizes
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
        val resolvedChildrenSizePhase = resolveFlowChildSizes(reversed, nothingForChild(), height, availableWidth, availableHeight, children)
        val childHeight = resolvedChildrenSizePhase.maxOfOrNull { fixFromChildAny(it.height) } ?: 0f
        val sumWidth = resolvedChildrenSizePhase.fold(0f) { a, b -> a + fixFromChild(b.width) }
        val totalWidth = sumWidth + spacingValue * (children.size - 1)

        ResolvedComponentSizePhase(fixForParent(totalWidth), fixForParentAny(childHeight)) { r ->
            var lastX = 0f
            val separators = mutableListOf<Float>()
            val resolvedChildrenPositionPhase = resolvedChildrenSizePhase.map { c ->
                val thisX = lastX

                if (showSeparators) separators += thisX + floor(-spacingValue + (spacingValue - separatorThickness) / 2)
                lastX += round(fixFromChild(c.width) + spacingValue)

                c.positionResolver(r
                    .resizeTo(width = fixFromChild(c.width).px, height = (fixFromChildAnyOptional(c.height) ?: r.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = r.x + thisX))
            }

            separators.removeFirstOrNull()

            ResolvedComponentPositionPhase(r, joinEventHandlers(eventHandlers, resolvedChildrenPositionPhase)) {
                for (f in drawFunctions) f(this)

                for (s in separators) withRegion(r.copy(
                    x = r.x + s + floor(-spacingValue + (spacingValue - separatorThickness) / 2),
                    width = separatorThickness
                )) { fill(separatorColour) }

                for (child in resolvedChildrenPositionPhase) {
                    withRegion(child.region, draw = child.draw)
                }
            }
        }
    }
}
