package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.extensions.ModifiedSizes
import com.exerro.simpleui.ui.extensions.modifier

@UndocumentedExperimentalUI
fun <Model: UIModel, Width: WhoDefinesMe, Height: WhoDefinesMe>
ComponentChildrenContext<Model, Width, Height>.withDecoration(
    after: Boolean = false,
    decoration: DrawContext.() -> Unit,
) = withDrawModifier { draw ->
    if (!after) decoration()
    draw(this)
    if (after) decoration()
}
