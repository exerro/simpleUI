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
        val resolvedChildrenSizePhase = resolveFlowChildSizes(reversed, width, nothingForChild(), availableWidth, availableHeight, children)
        val childWidth = resolvedChildrenSizePhase.maxOfOrNull { fixFromChildAny(it.width) } ?: 0f
        val sumHeight = resolvedChildrenSizePhase.fold(0f) { a, b -> a + fixFromChild(b.height) }
        val totalHeight = sumHeight + spacingValue * (children.size - 1)

        ResolvedComponentSizePhase(fixForParentAny(childWidth), fixForParent(totalHeight)) { r ->
            var lastY = 0f
            val separators = mutableListOf<Float>()
            val resolvedChildrenPositionPhase = resolvedChildrenSizePhase.map { c ->
                val thisY = lastY

                if (showSeparators) separators += thisY + floor(-spacingValue + (spacingValue - separatorThickness) / 2)
                lastY += round(fixFromChild(c.height) + spacingValue)

                c.positionResolver(r
                    .resizeTo(height = fixFromChild(c.height).px, width = (fixFromChildAnyOptional(c.width) ?: r.width).px, horizontalAlignment = horizontalAlignment)
                    .copy(y = r.y + thisY))
            }

            separators.removeFirstOrNull()

            ResolvedComponentPositionPhase(r, joinEventHandlers(eventHandlers, resolvedChildrenPositionPhase)) {
                for (f in drawFunctions) f(this)

                for (s in separators) withRegion(r.copy(
                    y = r.y + s + floor(-spacingValue + (spacingValue - separatorThickness) / 2),
                    height = separatorThickness
                )) { fill(separatorColour) }

                for (child in resolvedChildrenPositionPhase) {
                    withRegion(child.region, draw = child.draw)
                }
            }
        }
    }
}
