package com.exerro.simpleui

@Undocumented
interface DrawContext {
    @Undocumented
    val region: Region

    @Undocumented
    val id: StaticIdentifier?

    @Undocumented
    fun fill(
        colour: PaletteColour,
        opacity: Float = 1f
    )

    @Undocumented
    fun roundedRectangle(
        cornerRadius: Float,
        colour: PaletteColour,
        borderColour: PaletteColour = colour,
        borderWidth: Float = 0f, // TODO: make this pixels not float
    )

    @Undocumented
    fun shadow(
        colour: PaletteColour = PaletteColour.Black(PaletteVariant.Lighter),
        radius: Float = 10f,
        offset: Float = 2f,
        cornerRadius: Float = 0f,
    )

    @Undocumented
    fun write(
        text: FormattedText<*>,
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        wrap: Boolean = false,
    )

    @Undocumented
    fun write(
        text: String,
        colour: PaletteColour = PaletteColour.White(),
        font: Font = Font.default,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        wrap: Boolean = false,
    ) {
        val ftext = text.split('\n').map { line ->
            line.split(' ').map { FormattedText.text(it, colour) } .flatten(FormattedText.whitespace)
        } .flatten(FormattedText.lineBreak)
         return write(ftext, font, horizontalAlignment, verticalAlignment, wrap)
    }


    @Undocumented
    fun image(
        path: String,
        horizontalAlignment: Alignment = 0.5f,
        verticalAlignment: Alignment = 0.5f,
        stretchToFit: Boolean = false,
    )

    @Undocumented
    fun Region.draw(
        clip: Boolean = false,
        id: StaticIdentifier? = null,
        mount: MountPoint? = null,
        draw: DrawContext.() -> Unit
    )
}
