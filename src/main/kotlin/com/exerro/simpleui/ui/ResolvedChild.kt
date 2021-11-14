package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental

@UndocumentedExperimental
data class ResolvedChild<out W: Float?, out H: Float?>(
    val width: W,
    val height: H,
    val draw: DrawContext.() -> Unit,
)
