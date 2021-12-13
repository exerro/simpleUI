package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentChildrenContext<Model, Width, Height>.withTranslation(
    dx: Pixels = 0.px,
    dy: Pixels = 0.px,
) = withDrawModifier { draw -> withRegion(region.translateBy(dx = dx, dy = dy), draw = draw) }
