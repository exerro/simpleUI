package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.GenericResolver

@UndocumentedExperimentalUI
@UIContextType
interface ComponentContext<
        Model: UIModel,
        Width: WhoDefinesMe,
        Height: WhoDefinesMe,
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
            width: SomeValueForChild<Width>,
            height: SomeValueForChild<Height>,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
        ) -> SizeResolvedComponent<Width, Height>,
    ): ComponentIsResolved

    @UndocumentedExperimentalUI
    // TODO: add extra configuration for controlling child tracking
    fun <SubWidth: WhoDefinesMe, SubHeight: WhoDefinesMe> children(
        getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
        resolveComponent: (
            width: SomeValueForChild<Width>,
            height: SomeValueForChild<Height>,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
            children: List<GenericResolver<SubWidth, SubHeight>>
        ) -> SizeResolvedComponent<Width, Height>,
    ): ComponentIsResolved
}
