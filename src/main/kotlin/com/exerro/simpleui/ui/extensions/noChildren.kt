package com.exerro.simpleui.ui.extensions

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*

@UndocumentedExperimentalUI
fun ComponentContext<*, ParentDefinesMe, ParentDefinesMe>.noChildren() = setResolver { _, _, _, _, drawFunctions, eventHandlers ->
    ResolvedComponentSizePhase(nothingForParent(), nothingForParent()) { r ->
        ResolvedComponentPositionPhase(r, eventHandlers) { for (f in drawFunctions) f(this) } }
}

@UndocumentedExperimentalUI
inline fun <Width: WhoDefinesMe, Height: WhoDefinesMe> ComponentContext<*, Width, Height>.noChildren(
    crossinline resolveChildSize: (width: SizeForChild<Width>, height: SizeForChild<Height>, availableWidth: Float, availableHeight: Float) -> Pair<SizeForParent<Width>, SizeForParent<Height>>
) = setResolver { w, h, aw, ah, drawFunctions, eventHandlers ->
    val (cw, ch) = resolveChildSize(w, h, aw, ah)
    ResolvedComponentSizePhase(cw, ch) { r ->
        ResolvedComponentPositionPhase(r, eventHandlers) { for (f in drawFunctions) f(this) } }
}

@UndocumentedExperimentalUI
inline fun <reified Width: WhoDefinesMe, reified Height: WhoDefinesMe> ComponentContext<*, Width, Height>.noChildrenDefineDefaultSize(
    width: Float,
    height: Float,
) = setResolver { w, h, _, _, drawFunctions, eventHandlers ->
    val cw = fixForParentAny<Width>(fixFromParentAnyOptional(w) ?: width)
    val ch = fixForParentAny<Height>(fixFromParentAnyOptional(h) ?: height)
    ResolvedComponentSizePhase(cw, ch) { r ->
        ResolvedComponentPositionPhase(r, eventHandlers) { for (f in drawFunctions) f(this) } }
}

@UndocumentedExperimentalUI
inline fun <reified Width: WhoDefinesMe> ComponentContext<*, Width, ParentDefinesMe>.noChildrenDefineDefaultWidth(
    width: Float
) = setResolver { w, _, _, _, drawFunctions, eventHandlers ->
    val cw = fixForParentAny<Width>(fixFromParentAnyOptional(w) ?: width)
    ResolvedComponentSizePhase(cw, nothingForParent()) { r ->
        ResolvedComponentPositionPhase(r, eventHandlers) { for (f in drawFunctions) f(this) } }
}

@UndocumentedExperimentalUI
inline fun <reified Height: WhoDefinesMe> ComponentContext<*, ParentDefinesMe, Height>.noChildrenDefineDefaultHeight(
    height: Float
) = setResolver { _, h, _, _, drawFunctions, eventHandlers ->
    val ch = fixForParentAny<Height>(fixFromParentAnyOptional(h) ?: height)
    ResolvedComponentSizePhase(nothingForParent(), ch) { r ->
        ResolvedComponentPositionPhase(r, eventHandlers) { for (f in drawFunctions) f(this) } }
}
