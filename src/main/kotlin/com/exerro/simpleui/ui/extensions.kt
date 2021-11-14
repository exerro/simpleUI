package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimental
data class ModifiedSizes<Width: Float?, Height: Float?>(
    val width: Width,
    val height: Height,
    val availableWidth: Float,
    val availableHeight: Float,
)

@UndocumentedExperimental
fun <ChildWidth: Float?, ChildHeight: Float?> ComponentContext<*, *, ChildWidth, ChildHeight>.noChildren(
    declareWidth: ChildWidth,
    declareHeight: ChildHeight
) = children<Nothing?, Nothing?, Nothing?, Nothing?>({}) { _, _, _, _, drawFunctions, _ ->
    ResolvedChild(declareWidth, declareHeight) {
        for (f in drawFunctions) f(this)
    }
}

@UndocumentedExperimental
fun ComponentContext<*, *, Nothing?, Nothing?>.noChildren() =
    noChildren(null, null)

@UndocumentedExperimental
@JvmName("noChildrenW")
fun ComponentContext<*, *, Float, Nothing?>.noChildren(declareWidth: Float) =
    noChildren(declareWidth, null)

@UndocumentedExperimental
@JvmName("noChildrenH")
fun ComponentContext<*, *, Nothing?, Float>.noChildren(declareHeight: Float) =
    noChildren(null, declareHeight)

@UndocumentedExperimental
fun <OldParentWidth: Float?, OldParentHeight: Float?, OldChildWidth: Float?, OldChildHeight: Float?, NewParentWidth: Float?, NewParentHeight: Float?, NewChildWidth: Float?, NewChildHeight: Float?> ParentContext<OldParentWidth, OldParentHeight, OldChildWidth, OldChildHeight>.modifier(
    modifyParentSize: (OldParentWidth, OldParentHeight, Float, Float) -> ModifiedSizes<NewParentWidth, NewParentHeight>,
    modify: (OldParentWidth, OldParentHeight, Float, Float, ModifiedSizes<NewParentWidth, NewParentHeight>, ResolvedChild<NewChildWidth, NewChildHeight>) -> ResolvedChild<OldChildWidth, OldChildHeight>
): ParentContext<NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
    val parentContext = this
    return object : ParentContext<NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
        override fun rawComponent(
            elementType: String,
            trackingId: Any?,
            init: ComponentContext<NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight>.() -> ChildReturn
        ) = parentContext.rawComponent(elementType, trackingId) {
            val pContext = this

            object: ComponentContext<NewParentWidth, NewParentHeight, NewChildWidth, NewChildHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override fun refresh() = pContext.refresh()
                override fun <ParentHeight : HookState> getHookStateOrRegister(newHook: () -> ParentHeight) = pContext.getHookStateOrRegister(newHook)
                override fun onDraw(draw: DrawContext.() -> Unit) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: (WindowEvent) -> Unit) = pContext.connectEventHandler(handler)

                override fun <SubParentWidth : Float?, SubParentHeight : Float?, SubChildWidth : Float?, SubChildHeight : Float?> children(
                    getChildren: ParentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
                    resolveChildren: (NewParentWidth, NewParentHeight, Float, Float, List<DrawContext.() -> Unit>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedChild<SubChildWidth, SubChildHeight>>) -> ResolvedChild<NewChildWidth, NewChildHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, children ->
                    val m = modifyParentSize(width, height, availableWidth, availableHeight)
                    modify(width, height, availableWidth, availableHeight, m, resolveChildren(m.width, m.height, m.availableWidth, m.availableHeight, drawFunctions, children))
                }
            } .init()
        }
    }
}

@UndocumentedExperimental
fun <OldParentWidth: Float?, OldParentHeight: Float?, OldChildWidth: Float?, OldChildHeight: Float?, NewChildWidth: Float?, NewChildHeight: Float?> ParentContext<OldParentWidth, OldParentHeight, OldChildWidth, OldChildHeight>.modifier(
    modify: (OldParentWidth, OldParentHeight, Float, Float, ResolvedChild<NewChildWidth, NewChildHeight>) -> ResolvedChild<OldChildWidth, OldChildHeight>
): ParentContext<Nothing?, Nothing?, NewChildWidth, NewChildHeight> {
    val parentContext = this
    return object : ParentContext<Nothing?, Nothing?, NewChildWidth, NewChildHeight> {
        override fun rawComponent(
            elementType: String,
            trackingId: Any?,
            init: ComponentContext<Nothing?, Nothing?, NewChildWidth, NewChildHeight>.() -> ChildReturn
        ) = parentContext.rawComponent(elementType, trackingId) {
            val pContext = this

            object: ComponentContext<Nothing?, Nothing?, NewChildWidth, NewChildHeight> {
                override val thisComponentId get() = pContext.thisComponentId
                override fun refresh() = pContext.refresh()
                override fun <ParentHeight : HookState> getHookStateOrRegister(newHook: () -> ParentHeight) = pContext.getHookStateOrRegister(newHook)
                override fun onDraw(draw: DrawContext.() -> Unit) = pContext.onDraw(draw)
                override fun connectEventHandler(handler: (WindowEvent) -> Unit) = pContext.connectEventHandler(handler)

                override fun <SubParentWidth : Float?, SubParentHeight : Float?, SubChildWidth : Float?, SubChildHeight : Float?> children(
                    getChildren: ParentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
                    handleChildren: (Nothing?, Nothing?, Float, Float, List<DrawContext.() -> Unit>, List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedChild<SubChildWidth, SubChildHeight>>) -> ResolvedChild<NewChildWidth, NewChildHeight>
                ) = pContext.children(getChildren) { width, height, availableWidth, availableHeight, drawFunctions, children ->
                    modify(width, height, availableWidth, availableHeight, handleChildren(null, null, availableWidth, availableHeight, drawFunctions, children))
                }
            } .init()
        }
    }
}
