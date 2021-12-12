package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.internal.GenericResolver
import com.exerro.simpleui.ui.internal.standardChildRendering

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
        resolveComponentSize: (
            width: SomeValueForChild<Width>,
            height: SomeValueForChild<Height>,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
        ) -> SizeResolvedComponent<Width, Height>,
    ): ComponentIsResolved

    @UndocumentedExperimentalUI
    fun <SubWidth: WhoDefinesMe, SubHeight: WhoDefinesMe> children(
        getChildren: ComponentChildrenContext<Model, SubWidth, SubHeight>.() -> Unit,
        resolveComponentSize: (
            width: SomeValueForChild<Width>,
            height: SomeValueForChild<Height>,
            availableWidth: Float,
            availableHeight: Float,
            drawFunctions: List<ComponentDrawFunction>,
            eventHandlers: List<ComponentEventHandler>,
            children: List<GenericResolver<SubWidth, SubHeight>>
        ) -> SizeResolvedComponent<Width, Height>,
    ): ComponentIsResolved

    @UndocumentedExperimentalUI
    val singleChild: ComponentChildContext<Model, Width, Height> get() =
        object: ComponentChildContext<Model, Width, Height>, ComponentContext<Model, Width, Height> by this@ComponentContext {
            override val ids = IdProvider(this@ComponentContext.thisComponentId)

            override fun component(
                elementType: String,
                id: Id,
                init: ComponentContext<Model, Width, Height>.() -> ComponentIsResolved
            ): ComponentIsResolved {
                return this@ComponentContext.children<Width, Height>(
                    { component(elementType, id, init) },
                    { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, children ->
                        val child = children[0](width, height, availableWidth, availableHeight)

                        SizeResolvedComponent(child.width, child.height) { r ->
                            standardChildRendering(r, drawFunctions, eventHandlers, listOf(child.positionResolver(r)))
                        }
                    }
                )
            }
        }
}
