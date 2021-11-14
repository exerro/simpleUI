package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimental
@UIContextType
interface ComponentContext<
        /** Width provided by parent to this component. */
        ParentWidth: Float?,
        /** Height provided by parent to this component. */
        ParentHeight: Float?,
        /** Width provided by this component to parent. */
        ChildWidth: Float?,
        /** Height provided by this component to parent. */
        ChildHeight: Float?,
> {
    @UndocumentedExperimental
    val thisComponentId: Any?

    @UndocumentedExperimental
    fun refresh()

    @UndocumentedExperimental
    fun <H: HookState> getHookStateOrRegister(newHook: () -> H): H

    @UndocumentedExperimental
    fun onDraw(draw: DrawContext.() -> Unit)

    @UndocumentedExperimental
    fun connectEventHandler(handler: (WindowEvent) -> Unit)

    @UndocumentedExperimental
    // TODO: add extra configuration for controlling child tracking
    fun <SubParentWidth: Float?, SubParentHeight: Float?, SubChildWidth: Float?, SubChildHeight: Float?> children(
        getChildren: ParentContext<SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
        resolveChildren: (
            width: ParentWidth,
            height: ParentHeight,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<DrawContext.() -> Unit>,
            children: List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedChild<SubChildWidth, SubChildHeight>>
        ) -> ResolvedChild<ChildWidth, ChildHeight>,
    ): ChildReturn
}
