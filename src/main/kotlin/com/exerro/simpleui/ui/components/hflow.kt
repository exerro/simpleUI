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
) = rawComponent("hflow") {
    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

    children(init) { _, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
        val spacingValue = spacing.apply(availableWidth) + separatorThickness
        val appliedChildren = (if (reversed) children.reversed() else children).map { child ->
            child(nothingForChild(), height, availableWidth, availableHeight)
        }
        val childHeight = if (appliedChildren.isNotEmpty()) appliedChildren.maxOf { fixFromChildAny(it.height) } else 0f
        val sumWidth = appliedChildren.fold(0f) { a, b -> a + fixFromChild(b.width) }
        val totalWidth = sumWidth + spacingValue * (children.size - 1)

        SizeResolvedComponent(fixForParent(totalWidth), fixForParentAny(childHeight), joinEventHandlers(eventHandlers, appliedChildren)) {
            var lastX = 0f

            for (f in drawFunctions) f(this)

            appliedChildren.forEachIndexed { i, c ->
                if (showSeparators && i > 0)
                    withRegion(region.copy(
                        x = region.x + lastX - floor((spacingValue + separatorThickness) / 2),
                        width = separatorThickness
                    )) { fill(separatorColour) }

                withRegion(region
                    .resizeTo(width = fixFromChild(c.width).px, height = (fixFromChildAnyOptional(c.height) ?: region.height).px, verticalAlignment = verticalAlignment)
                    .copy(x = region.x + lastX),
                    draw = c.draw)

                lastX += round(fixFromChild(c.width) + spacingValue)
            }
        }
    }
}
