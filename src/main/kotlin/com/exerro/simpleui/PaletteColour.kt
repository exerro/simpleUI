package com.exerro.simpleui

/** A collection of standard colours. Each palette colour has a [PaletteVariant]
 *  allowing minor changes to the standard colour. */
sealed interface PaletteColour {
    val variant: PaletteVariant

    /** Return a copy of this colour with a different [PaletteVariant]. */
    fun withVariant(variant: PaletteVariant): PaletteColour

    data class Black(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Charcoal(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
//    data class Grey(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Silver(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class White(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }

    data class Red(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Orange(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Yellow(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Green(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Teal(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Blue(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Purple(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Pink(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
}
