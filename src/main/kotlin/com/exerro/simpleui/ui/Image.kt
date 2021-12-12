package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
data class Image(
    val image: String,
    val imageIsResource: Boolean = true
)
