package com.exerro.simpleui.experimental.animation

import com.exerro.simpleui.UndocumentedInternal
import kotlin.math.sqrt
import kotlin.time.Duration

@UndocumentedInternal
data class Animation<T>(
    @UndocumentedInternal
    val to: T,
    @UndocumentedInternal
    val duration: Duration = Duration.Companion.milliseconds(250),
    @UndocumentedInternal
    val easing: Easing = Easing.LINEAR,
    @UndocumentedInternal
    val interpolator: (Float, T, T) -> T,
) {
    @UndocumentedInternal
    fun interface Easing {
        @UndocumentedInternal
        fun fix(t: Float): Float

        companion object {
            @UndocumentedInternal
            val LINEAR = Easing { t -> t }

            @UndocumentedInternal
            val IN = Easing { t -> sqrt(t) }

            @UndocumentedInternal
            val OUT = Easing { t -> t * t }

            @UndocumentedInternal
            val BETWEEN = Easing { t -> 3 * t * t - 2 * t * t * t }
        }
    }
}
