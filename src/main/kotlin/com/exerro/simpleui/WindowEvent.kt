package com.exerro.simpleui

@Undocumented
sealed interface WindowEvent

@Undocumented
object EWindowClosed: WindowEvent {
    override fun toString() = this::class.simpleName!!
}

@Undocumented
data class EKeyPressed(
    val name: String,
    val scancode: Int,
    val isRepeat: Boolean,
    val modifiers: Set<KeyModifier>,
): WindowEvent

@Undocumented
data class EKeyReleased(
    val name: String,
    val scancode: Int,
    val modifiers: Set<KeyModifier>,
): WindowEvent

@Undocumented
data class ETextInput(
    val content: String,
): WindowEvent
