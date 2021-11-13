package com.exerro.simpleui.extensions

import com.exerro.simpleui.*

/** Filter the events from the [EventBus] for [EKeyPressed] events exactly
 *  matching the [name] and [modifiers] given, and accepting repeats only if
 *  [allowRepeats] is true. */
fun EventBus<WindowEvent>.filterKeyPressed(
    name: String,
    vararg modifiers: KeyModifier,
    allowRepeats: Boolean = true,
): EventBus<EKeyPressed> = filterIsInstance<EKeyPressed>()
    .filter { it.name == name }
    .filter { !it.isRepeat || allowRepeats }
    .filter { it.modifiers == modifiers.toSet() }
