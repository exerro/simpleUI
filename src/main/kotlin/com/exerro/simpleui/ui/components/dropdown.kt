package com.exerro.simpleui.ui.components

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.modifiers.withDecoration
import com.exerro.simpleui.ui.modifiers.withLayer
import com.exerro.simpleui.ui.modifiers.withVerticalAlignment2
import com.exerro.simpleui.ui.standardActions.MoveFocusDown
import com.exerro.simpleui.ui.standardActions.MoveFocusUp
import com.exerro.simpleui.ui.standardActions.SelectEntity

// TODO: escape to close
// TODO: context groupUpdates { ... } to avoid multiple refreshes

@UndocumentedExperimentalUI
fun <T, Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>
ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.dropdown(
    initialSelectedOption: T,
    options: List<T>,
    focused: Boolean = false,
    spacing: Pixels = 0.px,
    horizontalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = true,
    onOptionChanged: (T) -> Unit = {},
    toggleVisibleAction: Action = SelectEntity,
    selectNextOptionAction: Action = MoveFocusDown, // TODO
    selectPreviousOptionAction: Action = MoveFocusUp, // TODO
    renderOption: DeferredComponentContext<Model, ParentWidth, Nothing?, ChildWidth, Float>.(T) -> ComponentReturn,
    renderPrimaryOption: DeferredComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.(T) -> ComponentReturn,
) = component("dropdown") {
    val (selectedOption, setSelectedOption) = useState(initialSelectedOption, updateOnVaryingInitialValue = true)
    val (stateIsExpanded, setExpandedState) = useState(false)
    val (selectedOptionIndex, setSelectedOptionIndex) = useState(0)
    val cornerRadius = 8.px
    val isExpanded = stateIsExpanded && focused

    if (focused) bind(toggleVisibleAction) {
        setExpandedState(!isExpanded)
        if (isExpanded && selectedOptionIndex in options.indices) {
            val option = options[selectedOptionIndex]
            onOptionChanged(option)
            setSelectedOption(option)
        }
        else if (!isExpanded) {
            setSelectedOptionIndex(options.indexOf(selectedOption).takeIf { it != -1 } ?: 0)
        }
        true
    }

    if (focused && isExpanded) bind(selectNextOptionAction) {
        if (selectedOptionIndex < options.lastIndex) setSelectedOptionIndex(selectedOptionIndex + 1)
        true
    }

    if (focused && isExpanded) bind(selectPreviousOptionAction) {
        if (selectedOptionIndex > 0) setSelectedOptionIndex(selectedOptionIndex - 1)
        true
    }

    if (isExpanded) {
        lateinit var parentRegion: Region

        withLayer(Layer.Foreground).withVerticalAlignment2(verticalAlignment = 0f).withDecoration {
            parentRegion = region
            shadow(cornerRadius = cornerRadius, colour = model.style[Style.ShadowColour])
            roundedRectangle(cornerRadius = cornerRadius, colour = model.style[Style.ElementBackgroundColour])
        } .vflow(
            spacing = spacing,
            reversed = false,
            horizontalAlignment = horizontalAlignment,
            showSeparators = showSeparators,
        ) {
            for ((i, option) in options.withIndex()) {
                component(elementType = "dropdown-option") {
                    if (selectedOptionIndex == i) onDraw {
                        withRegion(region, clip = true) {
                            withRegion(parentRegion) {
                                roundedRectangle(cornerRadius = cornerRadius, colour = model.style[Style.PrimaryBackgroundColour])
                            }
                        }
                    }

                    renderOption(this, option)
                }
            }
        }
    }
    else {
        onDraw {
            shadow(cornerRadius = cornerRadius, colour = model.style[Style.ShadowColour])
            roundedRectangle(cornerRadius = cornerRadius, colour = model.style[Style.ElementBackgroundColour])

            withRegion(region.resizeTo(width = region.height.px, horizontalAlignment = 1f).resizeTo(width = 40.percent, height = 40.percent)) {
                image("images/expand.png", model.style[Style.ElementForegroundColour], isResource = true)
            }

            if (focused)
                withRegion(region.resizeTo(height = 2.px, width = 100.percent - cornerRadius * 2f, verticalAlignment = 1f), clip = true) {
                    withRegion(region.resizeTo(height = 32.px, verticalAlignment = 0f)) {
                        roundedRectangle(cornerRadius = 4.px, colour = model.style[Style.PrimaryBackgroundColour])
                    }
                }
        }

        renderPrimaryOption(selectedOption)
    }
}
