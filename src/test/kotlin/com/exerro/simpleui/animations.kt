package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Hello World")
    val mountPoints = listOf(MountPoint.Left, MountPoint.Bottom, MountPoint.InPlace, MountPoint.Top, MountPoint.Right)
    val palette = Palette.Default
    var active = true

    fun draw() = if (active) window.draw {
        val (topHalfRaw, bottomHalfRaw) = region.splitVertically()
        val topHalf = topHalfRaw.withPadding(16.px)
        val bottomHalf = bottomHalfRaw.withPadding(16.px)
        val unclippedRegions = topHalf.partitionHorizontally(mountPoints.size, spacing = 16.px)
        val clippedRegions = bottomHalf.partitionHorizontally(mountPoints.size, spacing = 16.px)
        val palette = Palette.Default

        fill(palette[PaletteColour.White()])

        for ((r, m) in unclippedRegions zip mountPoints) {
            r.draw(id = StaticIdentifier("noclip/$m"), mount = m) {
                fill(palette[PaletteColour.Teal()])
                write(m.name, palette[PaletteColour.White()])
            }
        }

        for ((r, m) in clippedRegions zip mountPoints) {
            r.draw(clip = true) {
                r.draw(id = StaticIdentifier("clip/$m"), mount = m) {
                    fill(palette[PaletteColour.Red()])
                    write(m.name, palette[PaletteColour.White()])
                }
            }
        }
    }
    else window.draw {
        val popup = region.withPadding(left = 25.percent, right = 25.percent, top = 10.percent, bottom = 40.percent)

        fill(palette[PaletteColour.Charcoal(PaletteVariant.Lighter)])

        popup.draw(id = StaticIdentifier("popup"), mount = MountPoint.Top) {
            shadow(cornerRadius = 4.px)
            roundedRectangle(cornerRadius = 4.px, palette[PaletteColour.Charcoal()])
            region.resizeTo(width = 100.px, height = 100.px).draw {
                roundedRectangle(5.px, palette[PaletteColour.Yellow()], borderWidth = 4.px, borderColour = palette[PaletteColour.Orange()])
                region.withPadding(12.px).draw {
                    write("Hello, I am in a popup!!", palette[PaletteColour.White()], wrap = true)
                }
            }
        }
    }

    window.events.filterIsInstance<EKeyPressed>().connect {
        active = !active
        draw()
    }

    draw()

    while (!window.isClosed) {
        GLFWWindowCreator.update()
    }
}
