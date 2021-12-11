package com.exerro.simpleui

@Undocumented
interface Layer {
    @Undocumented
    val name: String

    @Undocumented
    object Default: Layer {
        override val name = "Default"
    }
}
