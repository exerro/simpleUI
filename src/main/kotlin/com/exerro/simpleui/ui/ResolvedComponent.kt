package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
data class ResolvedComponent<out W: Float?, out H: Float?>(
    val width: W,
    val height: H,
    val eventHandlers: List<ComponentEventHandler>,
    val draw: ComponentDrawFunction,
)
