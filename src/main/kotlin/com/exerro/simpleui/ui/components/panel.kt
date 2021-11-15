package com.exerro.simpleui.ui.components

import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.ui.ParentDefinedContext
import com.exerro.simpleui.ui.noChildren

@UndocumentedExperimental
fun ParentDefinedContext<*>.panel(
    colour: Colour
) = rawComponent("panel") {
    onDraw { fill(colour) }
    noChildren()
}
