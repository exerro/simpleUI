package com.exerro.simpleui

@UndocumentedExperimental
interface Layer {
    @UndocumentedExperimental
    val name: String

    @UndocumentedExperimental
    object Default: Layer {
        override val name = "Default"
    }

    @UndocumentedExperimental
    object Foreground: Layer {
        override val name = "Foreground"
    }

    @UndocumentedExperimental
    object Background: Layer {
        override val name = "Background"
    }

    @UndocumentedExperimental
    object Debug: Layer {
        override val name = "Debug"
    }
}
