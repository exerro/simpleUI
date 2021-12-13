package com.exerro.simpleui.ui.internal

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
internal data class TransientComponentData<Width: WhoDefinesMe, Height: WhoDefinesMe>(
    val sizeResolver: ComponentSizeResolver<Width, Height>,
)
