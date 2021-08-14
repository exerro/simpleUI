package com.exerro.simpleui

@Undocumented
sealed interface PaletteColour {
    val variant: PaletteVariant

    fun withVariant(variant: PaletteVariant): PaletteColour

    data class Black(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
    data class Charcoal(override val variant: PaletteVariant = PaletteVariant.Default): PaletteColour { override fun withVariant(variant: PaletteVariant) = copy(variant = variant) }
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
