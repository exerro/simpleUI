package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Main")
    var toggle = true
    var alternatePosition = true
    val palette = Palette.Default

    fun draw() = window.draw {
        fill(palette[PaletteColour.Black()])

        val area = region
            .withPadding(16.px)
            .withPadding(left = if (alternatePosition) 128.px else 0.px)
            .resizeTo(256.px, 384.px, horizontalAlignment = 0f, verticalAlignment = 0f)
        val top = area.resizeTo(height = 60.percent, verticalAlignment = 0f)
        val bottom = area.resizeTo(height = 60.percent, verticalAlignment = 1f)

        if (toggle) {
            top.draw(id = StaticIdentifier("top"), mount = MountPoint.Left) {
                fill(palette[PaletteColour.Blue()])
            }
            // dontDraw(id = StaticIdentifier("bottom"))
        }
        else {
            // dontDraw(id = StaticIdentifier("top"))
            bottom.draw(id = StaticIdentifier("bottom"), mount = MountPoint.Left) {
                fill(palette[PaletteColour.Red()])
            }
        }
    }

    window.events.filterIsInstance<EKeyPressed>().connect {
        when (it.name) {
            "t" -> toggle = !toggle
            "p" -> alternatePosition = !alternatePosition
        }
        draw()
    }

    draw()

    while (!window.isClosed) GLFWWindowCreator.update()
}
