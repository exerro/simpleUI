package com.exerro.simpleui.internal

import com.exerro.simpleui.Undocumented
import org.lwjgl.nanovg.NVGColor
import java.nio.ByteBuffer

@Undocumented
internal class NVGData(
    val context: Long,
    val animation: AnimationHelper,
    val colour: NVGColor,
    val colour2: NVGColor,
    val monoBuffer: ByteBuffer,
    val sansBuffer: ByteBuffer,
)
