package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
class ComponentIsResolved private constructor() {
    companion object {
        internal val INSTANCE = ComponentIsResolved()
    }
}
