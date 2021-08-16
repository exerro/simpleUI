package com.exerro.simpleui

/** RGB colour values between 0 and 1. */
data class RGB(
    val red: Float,
    val green: Float,
    val blue: Float,
) {
    constructor(red: Int, green: Int, blue: Int):
            this(red / 255f, green / 255f, blue / 255f)
}
