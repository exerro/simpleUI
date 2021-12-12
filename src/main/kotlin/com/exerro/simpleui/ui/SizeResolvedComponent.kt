package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class SizeResolvedComponent<W: WhoDefinesMe, H: WhoDefinesMe>(
    val width: SomeValueForParent<W>,
    val height: SomeValueForParent<H>,
    val eventHandlers: List<ComponentEventHandler>,
    val draw: ComponentDrawFunction,
)
