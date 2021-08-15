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
        opacity: Float = 1f,
    )

    @Undocumented
    fun roundedRectangle(
        cornerRadius: Pixels,
        colour: PaletteColour,
        borderColour: PaletteColour = colour,
        borderWidth: Pixels = 0.px, // TODO: make this pixels not float
        opacity: Float = 1f,
    )

    @Undocumented
    fun ellipse(
        colour: PaletteColour,
        borderColour: PaletteColour = colour,
        borderWidth: Pixels = 0.px, // TODO: make this pixels not float
        opacity: Float = 1f,
    )

    @Undocumented
    fun shadow(
        colour: PaletteColour = PaletteColour.Black(PaletteVariant.Lighter),
        radius: Pixels = 10.px,
        offset: Pixels = 2f.px,
        cornerRadius: Pixels = 0.px,
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
        tint: PaletteColour? = null,
        isResource: Boolean = true,
    )

    @Undocumented
    fun Region.draw(
        clip: Boolean = false,
        id: StaticIdentifier? = null,
        mount: MountPoint? = null,
        draw: DrawContext.() -> Unit
    )

    @Undocumented
    fun List<Region>.draw(
        clip: Boolean = false,
        id: StaticIdentifier? = null,
        mount: MountPoint? = null,
        draw: DrawContext.(index: Int) -> Unit
    ) = forEachIndexed { i, r -> r.draw(clip = clip, id, mount) { draw(i) } }
}
