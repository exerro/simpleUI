package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.Region
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.event.WindowEvent

@UndocumentedExperimentalUI
sealed interface LifecycleHook {
    @UndocumentedExperimentalUI
    fun interface DrawHook: LifecycleHook {
        @UndocumentedExperimentalUI
        fun DrawContext.draw()
    }

    @UndocumentedExperimentalUI
    fun interface WindowEventHook: LifecycleHook {
        @UndocumentedExperimentalUI
        fun handleEvent(event: WindowEvent): Boolean
    }

    // TODO
    @UndocumentedExperimentalUI
    fun interface Positioned: LifecycleHook {
        @UndocumentedExperimentalUI
        fun onPositioned(region: Region)
    }

    @UndocumentedExperimentalUI
    fun interface LoadHook: LifecycleHook {
        @UndocumentedExperimentalUI
        fun onLoad()
    }

    @UndocumentedExperimentalUI
    fun interface UnloadHook: LifecycleHook {
        @UndocumentedExperimentalUI
        fun onUnload()
    }
}
