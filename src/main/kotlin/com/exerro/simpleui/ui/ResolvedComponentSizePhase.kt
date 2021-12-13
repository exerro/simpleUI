package com.exerro.simpleui.ui

import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class ResolvedComponentSizePhase<W: WhoDefinesMe, H: WhoDefinesMe>(
    val width: SizeForParent<W>,
    val height: SizeForParent<H>,
    val positionResolver: (Region) -> ResolvedComponentPositionPhase,
)
