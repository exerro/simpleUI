package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.ComponentInstance

@UndocumentedExperimentalUI
@UIContextType
interface ComponentContext<
        Model: UIModel,
        /** Width provided by parent to this component. */
        ParentWidth: Float?,
        /** Height provided by parent to this component. */
        ParentHeight: Float?,
        /** Width provided by this component to parent. */
        ChildWidth: Float?,
        /** Height provided by this component to parent. */
        ChildHeight: Float?,
>: SharedContext<Model> {
    @UndocumentedExperimentalUI
    val thisComponentId: Id

    @UndocumentedExperimentalUI
    fun refresh()

    @UndocumentedExperimentalUI
    fun <H: HookState> getHookStateOrNew(newHook: () -> H): H

    @UndocumentedExperimentalUI
    fun onDraw(draw: ComponentDrawFunction)

    @UndocumentedExperimentalUI
    fun connectEventHandler(handler: ComponentEventHandler)

    @UndocumentedExperimentalUI
    fun setResolver(
        resolveComponent: (
            width: ParentWidth,
            height: ParentHeight,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
        ) -> ResolvedComponent<ChildWidth, ChildHeight>,
    ): ComponentIsResolved

    @UndocumentedExperimentalUI
    // TODO: add extra configuration for controlling child tracking
    fun <SubParentWidth: Float?, SubParentHeight: Float?, SubChildWidth: Float?, SubChildHeight: Float?> children(
        getChildren: ComponentChildrenContext<Model, SubParentWidth, SubParentHeight, SubChildWidth, SubChildHeight>.() -> Unit,
        resolveComponent: (
            width: ParentWidth,
            height: ParentHeight,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
            children: List<(SubParentWidth, SubParentHeight, Float, Float) -> ResolvedComponent<SubChildWidth, SubChildHeight>>
        ) -> ResolvedComponent<ChildWidth, ChildHeight>,
    ): ComponentIsResolved
}
