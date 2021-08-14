package com.exerro.simpleui

@Undocumented
data class Font(
    @Undocumented
    val lineHeight: Float,
    @Undocumented
    val isMonospaced: Boolean,
) {
    companion object {
        @Undocumented
        val default = Font(lineHeight = 22f, isMonospaced = false)

        @Undocumented
        val monospace = Font(lineHeight = 18f, isMonospaced = true)

        @Undocumented
        val heading = Font(lineHeight = 28f, isMonospaced = true)
    }
}
