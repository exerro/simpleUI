package com.exerro.simpleui.ui

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class PositionResolvedComponent(
    val region: Region,
    val eventHandlers: List<ComponentEventHandler>,
    val draw: ComponentDrawFunction,
)
