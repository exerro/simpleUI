package com.exerro.simpleui.experimental.animation

import com.exerro.simpleui.UndocumentedInternal

@UndocumentedInternal
class AnimatedValue<T> internal constructor(
    initialValue: T,
    animation: Animation<T>? = null,
    clock: Long = 0L,
): Animated<T> {
    @UndocumentedInternal
    var clock = clock; private set

    @UndocumentedInternal
    var initialValue = initialValue; private set

    @UndocumentedInternal
    override var currentValue = initialValue; private set

    @UndocumentedInternal
    var animation = animation; private set

    @UndocumentedInternal
    override val isFinished get() = clock >= duration

    ////////////////////////////////////////////////////////////

    @UndocumentedInternal
    override fun update(dt: Long) = seek(clock + dt)

    @UndocumentedInternal
    fun seek(time: Long) {
        val a = animation ?: return
        clock = time
        if (clock > duration) clock = duration
        val t = (clock.toDouble() / duration).toFloat()
        currentValue = a.interpolator(a.easing.fix(t), initialValue, a.to) ?: currentValue
    }

    @UndocumentedInternal
    fun reset(animation: Animation<T>? = this.animation, clock: Long = 0L) {
        this.animation = animation
        this.clock = clock
        initialValue = currentValue
        duration = animation?.duration?.inWholeNanoseconds ?: 0L
    }

    @UndocumentedInternal
    fun reset(value: T) {
        animation = null
        clock = 0L
        initialValue = currentValue
        currentValue = value
        duration = 0L
    }

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    private var duration = animation?.duration?.inWholeNanoseconds ?: 0L
}
