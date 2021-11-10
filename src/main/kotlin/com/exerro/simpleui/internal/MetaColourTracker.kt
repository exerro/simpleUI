package com.exerro.simpleui.internal

import com.exerro.simpleui.colour.Colour
import com.exerro.simpleui.UndocumentedInternal
import com.exerro.simpleui.colour.Greyscale

@UndocumentedInternal
class MetaColourTracker {
    @UndocumentedInternal
    fun hasColour() =
        activeHighlightColours.isNotEmpty() || activeUnderlineColours.isNotEmpty() || activeStrikeThroughColours.isNotEmpty()

    @UndocumentedInternal
    fun pushHighlightColour(colour: Colour) {
        activeHighlightColours.add(colour)
    }

    @UndocumentedInternal
    fun pushUnderlineColour(colour: Colour) {
        activeUnderlineColours.add(colour)
    }

    @UndocumentedInternal
    fun pushStrikeThroughColour(colour: Colour) {
        activeStrikeThroughColours.add(colour)
    }

    @UndocumentedInternal
    fun popHighlightColour() {
        activeHighlightColours.removeLast()
    }

    @UndocumentedInternal
    fun popUnderlineColour() {
        activeUnderlineColours.removeLast()
    }

    @UndocumentedInternal
    fun popStrikeThroughColour() {
        activeStrikeThroughColours.removeLast()
    }

    @UndocumentedInternal
    fun currentHighlightColour() = activeHighlightColours.lastOrNull()

    @UndocumentedInternal
    fun currentUnderlineColour() = activeUnderlineColours.lastOrNull()

    @UndocumentedInternal
    fun currentStrikeThroughColour() = activeStrikeThroughColours.lastOrNull()

    ////////////////////////////////////////////////////////////////////////////

    @UndocumentedInternal
    private val activeHighlightColours = mutableListOf<Colour>()

    @UndocumentedInternal
    private val activeUnderlineColours = mutableListOf<Colour>()

    @UndocumentedInternal
    private val activeStrikeThroughColours = mutableListOf<Colour>()
}
