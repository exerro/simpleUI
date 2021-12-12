package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

typealias GenericResolver<Width, Height> = (
    SomeValueForChild<Width>,
    SomeValueForChild<Height>,
    Float,
    Float,
) -> SizeResolvedComponent<Width, Height>

@UndocumentedExperimentalUI
internal data class TransientComponentData<Width: WhoDefinesMe, Height: WhoDefinesMe>(
    val drawFunctions: List<ComponentDrawFunction>,
    val eventHandlers: List<ComponentEventHandler>,
    val sizeResolver: GenericResolver<Width, Height>,
)
