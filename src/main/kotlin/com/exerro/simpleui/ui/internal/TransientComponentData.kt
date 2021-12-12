package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.ComponentDrawFunction
import com.exerro.simpleui.ui.ComponentEventHandler
import com.exerro.simpleui.ui.ResolvedComponent

@UndocumentedExperimentalUI
internal data class TransientComponentData<ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>(
    val drawFunctions: List<ComponentDrawFunction>,
    val eventHandlers: List<ComponentEventHandler>,
    val resolver: (ParentWidth, ParentHeight, Float, Float) -> ResolvedComponent<ChildWidth, ChildHeight>
)
