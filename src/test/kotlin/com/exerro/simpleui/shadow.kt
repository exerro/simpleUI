package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Shadows")
    val palette = Palette.Default
    val white = palette[PaletteColour.White()]
    val silver = palette[PaletteColour.Silver()]
    val charcoal = palette[PaletteColour.Charcoal()]

    window.draw {
        val (left, right) = region.splitHorizontally()

        left.draw {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            fill(white)

            top.draw {
                region.resizeTo(256.px, 256.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, white)
                }
            }

            bottomLeft.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.White(PaletteVariant.Lighter)])
                }
            }

            bottomRight.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.White(PaletteVariant.Darker)])
                }
            }
        }

        right.draw {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            roundedRectangle(4.px, charcoal)

            top.draw {
                region.resizeTo(256.px, 256.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black(PaletteVariant.Lighter)], cornerRadius = 4.px)
                    roundedRectangle(4.px, charcoal)
                }
            }

            bottomLeft.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black()], cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.Charcoal(PaletteVariant.Darker)])
                }
            }

            bottomRight.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black(PaletteVariant.Lighter)], cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.Charcoal(PaletteVariant.Lighter)])
                }
            }
        }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
