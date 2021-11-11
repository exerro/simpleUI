package com.exerro.simpleui.animation

import com.exerro.simpleui.Undocumented

@Undocumented
class AnimatedValue<T> internal constructor(
    initialValue: T,
    animation: Animation<T>? = null,
    clock: Long = 0L,
): Animated<T> {
    @Undocumented
    var clock = clock; private set

    @Undocumented
    var initialValue = initialValue; private set

    @Undocumented
    override var currentValue = initialValue; private set

    @Undocumented
    var animation = animation; private set

    @Undocumented
    override val isFinished get() = clock >= duration

    ////////////////////////////////////////////////////////////

    @Undocumented
    override fun update(dt: Long) = seek(clock + dt)

    @Undocumented
    fun seek(time: Long) {
        val a = animation ?: return
        clock = time
        if (clock > duration) clock = duration
        val t = (clock.toDouble() / duration).toFloat()
        currentValue = a.interpolator(a.easing.fix(t), initialValue, a.to) ?: currentValue
    }

    @Undocumented
    fun reset(animation: Animation<T>? = this.animation, clock: Long = 0L) {
        this.animation = animation
        this.clock = clock
        initialValue = currentValue
        duration = animation?.duration?.inWholeNanoseconds ?: 0L
    }

    @Undocumented
    fun reset(value: T) {
        animation = null
        clock = 0L
        initialValue = currentValue
        currentValue = value
        duration = 0L
    }

    ////////////////////////////////////////////////////////////////////////////

    @Undocumented
    private var duration = animation?.duration?.inWholeNanoseconds ?: 0L
}
