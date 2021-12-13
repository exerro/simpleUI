package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import kotlin.reflect.KProperty

@UndocumentedExperimentalUI
interface PersistentStorageCell<T> {
    @UndocumentedExperimentalUI
    fun set(value: T)

    @UndocumentedExperimentalUI
    fun get(): T

    @UndocumentedExperimentalUI
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    @UndocumentedExperimentalUI
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)
}
