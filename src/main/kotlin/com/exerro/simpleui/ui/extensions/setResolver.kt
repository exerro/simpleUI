package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentContext<Model, Width, Height>.setResolver(
    resolveComponentSize: (
        width: SizeForChild<Width>,
        height: SizeForChild<Height>,
        availableWidth: Float,
        availableHeight: Float,
        drawFunctions: List<ComponentDrawFunction>,
        eventHandlers: List<ComponentEventHandler>,
    ) -> ResolvedComponentSizePhase<Width, Height>,
) = children<Width, Height>({}) { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, _ ->
    resolveComponentSize(width, height, availableWidth, availableHeight, drawFunctions, eventHandlers)
}
