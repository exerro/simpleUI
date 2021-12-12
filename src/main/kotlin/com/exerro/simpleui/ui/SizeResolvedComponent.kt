package com.exerro.simpleui.ui

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class SizeResolvedComponent<W: WhoDefinesMe, H: WhoDefinesMe>(
    val width: SomeValueForParent<W>,
    val height: SomeValueForParent<H>,
    val positionResolver: (Region) -> PositionResolvedComponent,
)
