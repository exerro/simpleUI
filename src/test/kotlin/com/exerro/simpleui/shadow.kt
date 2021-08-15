package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Shadows")

    window.draw {
        val (left, right) = region.splitHorizontally()

        left.draw {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            fill(PaletteColour.White())

            top.draw {
                region.resizeTo(256.px, 256.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Silver(), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.White())
                }
            }

            bottomLeft.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Silver(), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.White(PaletteVariant.Lighter))
                }
            }

            bottomRight.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Silver(), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.White(PaletteVariant.Darker))
                }
            }
        }

        right.draw {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            roundedRectangle(4.px, PaletteColour.Charcoal())

            top.draw {
                region.resizeTo(256.px, 256.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Black(PaletteVariant.Lighter), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.Charcoal())
                }
            }

            bottomLeft.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Black(), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.Charcoal(PaletteVariant.Darker))
                }
            }

            bottomRight.draw {
                region.resizeTo(height = 256.px).withPadding(horizontal = 128.px).draw {
                    shadow(radius = 10.px, offset = 2.px, colour = PaletteColour.Black(PaletteVariant.Lighter), cornerRadius = 4.px)
                    roundedRectangle(4.px, PaletteColour.Charcoal(PaletteVariant.Lighter))
                }
            }
        }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
