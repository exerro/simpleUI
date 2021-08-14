package com.exerro.simpleui

@Undocumented
sealed interface WindowEvent

@Undocumented
object EWindowClosed: WindowEvent {
    override fun toString() = this::class.simpleName!!
}
