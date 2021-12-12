package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.ui.ParentDefinedContext
import com.exerro.simpleui.ui.noChildren

@UndocumentedExperimentalUI
fun ParentDefinedContext<*>.panel(
    colour: Colour
) = component("panel") {
    onDraw { fill(colour) }
    noChildren()
}
