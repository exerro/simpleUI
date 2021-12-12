package com.exerro.simpleui

import com.exerro.simpleui.experimental.Palette
import com.exerro.simpleui.experimental.PaletteColour
import com.exerro.simpleui.experimental.PaletteVariant

fun main() {
    val window = GLFWWindowCreator.createWindow("Shadows")
    val palette = Palette.Default
    val white = palette[PaletteColour.White()]
    val silver = palette[PaletteColour.Silver()]
    val charcoal = palette[PaletteColour.Charcoal()]

    window.draw {
        val (left, right) = region.splitHorizontally()

        withRegion(left) {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            fill(white)

            withRegion(top) {
                withRegion(region.resizeTo(256.px, 256.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, white)
                }
            }

            withRegion(bottomLeft) {
                withRegion(region.resizeTo(height = 256.px).withPadding(horizontal = 128.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.White(PaletteVariant.Lighter)])
                }
            }

            withRegion(bottomRight) {
                withRegion(region.resizeTo(height = 256.px).withPadding(horizontal = 128.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = silver, cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.White(PaletteVariant.Darker)])
                }
            }
        }

        withRegion(right) {
            val (top, bottom) = region.splitVertically()
            val (bottomLeft, bottomRight) = bottom.splitHorizontally()

            roundedRectangle(4.px, charcoal)

            withRegion(top) {
                withRegion(region.resizeTo(256.px, 256.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black(PaletteVariant.Lighter)], cornerRadius = 4.px)
                    roundedRectangle(4.px, charcoal)
                }
            }

            withRegion(bottomLeft) {
                withRegion(region.resizeTo(height = 256.px).withPadding(horizontal = 128.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black()], cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.Charcoal(PaletteVariant.Darker)])
                }
            }

            withRegion(bottomRight) {
                withRegion(region.resizeTo(height = 256.px).withPadding(horizontal = 128.px)) {
                    shadow(radius = 10.px, offset = 2.px, colour = palette[PaletteColour.Black(PaletteVariant.Lighter)], cornerRadius = 4.px)
                    roundedRectangle(4.px, palette[PaletteColour.Charcoal(PaletteVariant.Lighter)])
                }
            }
        }
    }

    while (!window.isClosed) GLFWWindowCreator.update()
}
