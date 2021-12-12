package com.exerro.simpleui.ui

import com.exerro.simpleui.KeyModifier
import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class ActionKeybind(
    val keyName: String,
    val modifiers: Set<KeyModifier> = emptySet(),
    val allowRepeats: Boolean = true,
)
