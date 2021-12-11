package com.exerro.simpleui.ui.components

import com.exerro.simpleui.Alignment
import com.exerro.simpleui.Pixels
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.colour.Colours
import com.exerro.simpleui.px
import com.exerro.simpleui.ui.*
import com.exerro.simpleui.ui.hooks.useState
import com.exerro.simpleui.ui.internal.calculateInverse
import com.exerro.simpleui.ui.internal.joinEventHandlers
import com.exerro.simpleui.ui.modifiers.tracked
import com.exerro.simpleui.ui.standardActions.MoveFocusDown
import com.exerro.simpleui.ui.standardActions.MoveFocusUp
import com.exerro.simpleui.ui.standardActions.SelectEntity
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round

// TODO: improved layout
// TODO: expand/contract icons
// TODO: better selection graphics
// TODO: escape to close
// TODO: context groupUpdates { ... } to avoid multiple refreshes
// TODO: Z indexing

@UndocumentedExperimental
fun <T, Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?>
ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.dropdown(
    initialSelectedOption: T,
    options: List<T>,
    focused: Boolean = false,
    spacing: Pixels = 8.px,
    horizontalAlignment: Alignment = 0.5f,
    showSeparators: Boolean = true,
    onOptionChanged: (T) -> Unit = {},
    verticalPadding: Pixels = 8.px,
    horizontalPadding: Pixels = 16.px,
    trackOptions: Boolean = true,
    toggleVisibleAction: Action = SelectEntity,
    selectNextOptionAction: Action = MoveFocusDown, // TODO
    selectPreviousOptionAction: Action = MoveFocusUp, // TODO
    renderOption: DeferredComponentContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.(T) -> ComponentReturn
) = rawComponent("dropdown") {
    val (selectedOption, setSelectedOption) = useState(initialSelectedOption, handleVaryingInitialValue = true)
    val (isExpanded, setExpandedState) = useState(false)
    val (selectedOptionIndex, setSelectedOptionIndex) = useState(0)

    val separatorThickness = model.style[Style.SeparatorThickness].toFloat()
    val separatorColour = model.style[Style.SeparatorColour]

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

    children<ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
        getChildren = {
            (if (trackOptions) tracked("selected") else this)
                .rawComponent { DeferredComponentContext(this).renderOption(selectedOption) }

            if (isExpanded) {
                for ((index, option) in options.withIndex()) {
                    (if (trackOptions) tracked(index) else this)
                        .rawComponent { DeferredComponentContext(this).renderOption(option) }
                }
            }
        },
        resolveComponent = { width, height, availableWidth, availableHeight, drawFunctions, eventHandlers, allChildren ->
            val paddingWidth = horizontalPadding.apply(availableWidth)
            val paddingHeight = verticalPadding.apply(availableHeight)
            val innerWidth = width?.let { it - paddingWidth * 2 } as ParentWidth
            val innerHeight = height?.let { it - paddingHeight * 2 } as ParentHeight
            val innerAvailableWidth = availableWidth - paddingWidth * 2
            val innerAvailableHeight = availableHeight - paddingHeight * 2
            val topOption = allChildren[0](innerWidth, innerHeight, innerAvailableWidth, innerAvailableHeight)
            val children = allChildren.drop(1).map { it(innerWidth, innerHeight, innerAvailableWidth, innerAvailableHeight) }
            val spacingValue = spacing.apply(availableHeight)
            val reportedWidth = calculateInverse<ChildWidth>(width) { (children + topOption).maxOf { it.width as Float } + paddingWidth * 2 }
            val reportedHeight = calculateInverse<ChildHeight>(height) { (topOption.height as Float) + paddingHeight * 2 }
            val activeChildren = if (isExpanded) listOf(topOption) else children
            val cornerRadius = (min((width ?: reportedWidth)!!, (height ?: reportedHeight)!!) / 2).px
            val totalHeight = if (!isExpanded) (height ?: reportedHeight)!! else children.fold((height ?: reportedHeight)!!) { a, c ->
                a + (c.height ?: innerHeight)!! + spacingValue
            }

            ResolvedComponent(reportedWidth, reportedHeight, joinEventHandlers(eventHandlers, activeChildren)) {
                for (f in drawFunctions) f(this)

                region.resizeTo(height = totalHeight.px, verticalAlignment = 0f).draw {
                    shadow(cornerRadius = cornerRadius, colour = model.style[Style.ShadowColour])
                    roundedRectangle(cornerRadius = cornerRadius, colour = model.style[Style.ElementBackgroundColour])
                }

                val childRegion = region.copy(
                    x = region.x + paddingWidth,
                    y = region.y + paddingHeight,
                    width = region.width - paddingWidth * 2,
                    height = region.height - paddingHeight * 2,
                )

                if (!isExpanded) childRegion.draw(draw = topOption.draw)

                var lastY = round((topOption.height ?: innerHeight)!! + spacingValue)

                if (isExpanded) for ((i, c) in children.withIndex()) {
                    if (showSeparators && i > 0)
                        childRegion.copy(
                            y = childRegion.y + lastY - floor((spacingValue + separatorThickness) / 2),
                            height = separatorThickness
                        ).draw { fill(separatorColour) }

                    childRegion
                        .resizeTo(width = (c.width ?: innerWidth)!!.px, horizontalAlignment = horizontalAlignment)
                        .copy(y = childRegion.y + lastY, height = (c.height ?: innerHeight)!!)
                        .draw {
                            if (i == selectedOptionIndex) fill(Colours.red)

                            c.draw(this)
                        }

                    lastY += round((c.height ?: innerHeight)!! + spacingValue)
                }
            }
        }
    )


//    val backgroundColourKey = when (type) {
//        ButtonType.Default -> Style.ElementBackgroundColour
//        ButtonType.Primary -> Style.PrimaryBackgroundColour
//        ButtonType.Disabled -> Style.DisabledBackgroundColour
//        ButtonType.Error -> Style.ErrorBackgroundColour
//    }
//    val foregroundColourKey = when (type) {
//        ButtonType.Default -> Style.ForegroundColour
//        ButtonType.Primary -> Style.PrimaryForegroundColour
//        ButtonType.Disabled -> Style.DisabledForegroundColour
//        ButtonType.Error -> Style.ErrorForegroundColour
//    }
//    val focusColourKey = when (type) {
//        ButtonType.Default -> Style.PrimaryBackgroundColour
//        else -> foregroundColourKey
//    }
//    val shadowColourKey = when (type) {
//        ButtonType.Default -> Style.ShadowColour
//        else -> Style.AlternateShadowColour
//    }
//    val cornerRadius = model.style[Style.ButtonCornerRadius]
//    val focusUnderlineThickness = model.style[Style.FocusUnderlineThickness].px
//    val shadowRadius = model.style[Style.ShadowRadius].px
//    val shadowOffset = model.style[Style.ShadowOffset].px
//
//    if (focused) bind(action) {
//        behaviour()
//        true
//    }
//
//    onDraw {
//        val buttonHeight = region.height
//
//        shadow(cornerRadius = cornerRadius, colour = model.style[shadowColourKey], radius = shadowRadius, offset = shadowOffset)
//        roundedRectangle(cornerRadius = cornerRadius, colour = model.style[backgroundColourKey])
//        write(text)
//
//        if (focused) {
//            region.resizeTo(height = focusUnderlineThickness, width = 80.percent, verticalAlignment = 1f).draw(clip = true) {
//                region.resizeTo(height = buttonHeight.px, verticalAlignment = 0f).draw {
//                    roundedRectangle(cornerRadius = cornerRadius, colour = model.style[focusColourKey])
//                }
//            }
//        }
//
//        if (icon != null) region
//            .resizeTo(width = region.height.px, horizontalAlignment = 0f)
//            .withPadding(6.px)
//            .draw { image(icon.image, model.style[foregroundColourKey], icon.imageIsResource) }
//    }
}
