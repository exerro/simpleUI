package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Layer
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withLayer(
    layer: Layer,
) = withDrawModifier { draw -> withLayer(layer, draw) }
