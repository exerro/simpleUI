package com.exerro.simpleui

/** A [Layer] is a conceptual plane on which content is drawn. [Layer]s are
 *  ordered using [LayerComposition]. */
abstract class Layer(
    /** Name of this [Layer], for debug purposes only. */
    val name: String,
) {
    /** Default layer. */
    object Default: Layer("Default")

    /** Layer that should appear in front of [Default]. */
    object Foreground: Layer("Foreground")

    /** Layer that should appear behind [Default]. */
    object Background: Layer("Background")

    /** Layer that should appear in front of everything, showing debug
     *  information. */
    object Debug: Layer("Debug")
}
