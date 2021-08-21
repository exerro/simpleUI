package com.exerro.simpleui.internal

import com.exerro.simpleui.UndocumentedInternal
import org.lwjgl.nanovg.NVGColor
import java.nio.ByteBuffer

@UndocumentedInternal
internal class NVGData(
    val context: Long,
    val colour: NVGColor,
    val colour2: NVGColor,
    val monoBuffer: ByteBuffer,
    val sansBuffer: ByteBuffer,
    val imageCache: MutableMap<String, Int>
)
