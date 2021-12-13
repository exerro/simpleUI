package com.exerro.simpleui

/** An event emitted by a [Window]. */
sealed interface WindowEvent

/** Emitted when the window is closed. */
object EWindowClosed: WindowEvent {
    override fun toString() = this::class.simpleName!!
}

/** Emitted when the window is resized. */
data class EWindowResized(
    val width: Int,
    val height: Int,
): WindowEvent

/** Emitted when a key is pressed. */
data class EKeyPressed(
    val name: String,
    val scancode: Int,
    val isRepeat: Boolean,
    val modifiers: Set<KeyModifier>,
): WindowEvent

/** Emitted when a key is released. */
data class EKeyReleased(
    val name: String,
    val scancode: Int,
    val modifiers: Set<KeyModifier>,
): WindowEvent

/** Emitted when text content is input by the user, e.g. by typing on a
 *  keyboard. */
data class ETextInput(
    val content: String,
): WindowEvent
