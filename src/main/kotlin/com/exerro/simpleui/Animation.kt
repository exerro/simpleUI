package com.exerro.simpleui

import kotlin.math.sqrt
import kotlin.time.Duration

@Undocumented
data class Animation<T>(
    @Undocumented
    val to: T,
    @Undocumented
    val duration: Duration = Duration.Companion.milliseconds(250),
    @Undocumented
    val easing: Easing = Easing.LINEAR,
    @Undocumented
    val interpolator: (Float, T, T) -> T,
) {
    @Undocumented
    fun interface Easing {
        @Undocumented
        fun fix(t: Float): Float

        companion object {
            @Undocumented
            val LINEAR = Easing { t -> t }

            @Undocumented
            val IN = Easing { t -> sqrt(t) }

            @Undocumented
            val OUT = Easing { t -> t * t }

            @Undocumented
            val BETWEEN = Easing { t -> 3 * t * t - 2 * t * t * t }
        }
    }
}
