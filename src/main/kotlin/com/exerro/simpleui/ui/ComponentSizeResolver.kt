package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
typealias ComponentSizeResolver<Width, Height> = (
    SizeForChild<Width>,
    SizeForChild<Height>,
    Float,
    Float,
) -> ResolvedComponentSizePhase<Width, Height>
