package com.exerro.simpleui

fun main() {
    val window = GLFWWindowCreator.createWindow("Hello World")

    window.events.connect(::println)

    while (!window.isClosed) {
        GLFWWindowCreator.update()

        if ((System.nanoTime() / 1000000000L % 4L) <= 2L) window.draw {
            val popup = region.withPadding(left = 25.percent, right = 25.percent, top = 10.percent, bottom = 40.percent)
            val (topHalfRaw, bottomHalfRaw) = region.splitVertically()
            val topHalf = topHalfRaw.withPadding(16.px)
            val bottomHalf = bottomHalfRaw.withPadding(16.px)
            val mountPoints = listOf(MountPoint.Left, MountPoint.Bottom, null, MountPoint.InPlace, MountPoint.Top, MountPoint.Right)
            val unclippedRegions = topHalf.partitionHorizontally(mountPoints.size, 16.px)
            val clippedRegions = bottomHalf.partitionHorizontally(mountPoints.size, 16.px)

            fill(PaletteColour.White())

            for ((r, m) in unclippedRegions zip mountPoints) {
                r.draw {
                    r.draw(id = StaticIdentifier("noclip/$m"), mount = m) {
                        fill(PaletteColour.Teal())
                    }
                }
            }

            for ((r, m) in clippedRegions zip mountPoints) {
                r.draw(clip = true) {
                    r.draw(id = StaticIdentifier("clip/$m"), mount = m) {
                        fill(PaletteColour.Red())
                    }
                }
            }

            popup.rounded().draw(id = StaticIdentifier("popup"), mount = MountPoint.Top) {
                fill(PaletteColour.Charcoal())
                region.resizeTo(width = 100.px, height = 100.px).rounded().draw {
                    roundedRectangle(5f, PaletteColour.Yellow(), borderWidth = 4f, borderColour = PaletteColour.Orange())
                    region.withPadding(12.px).draw {
                        write("Hello, I am in a popup!!", wrap = true)
                    }
                }
            }
        } else window.draw { fill(PaletteColour.White()) }
    }
}
