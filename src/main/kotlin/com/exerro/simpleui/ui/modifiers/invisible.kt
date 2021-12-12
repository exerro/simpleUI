package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.ui.*


@UndocumentedExperimentalUI
fun <Model: UIModel, ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ComponentChildrenContext<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight>.invisible() =
    modifier<Model, ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
        { w, h, availableWidth, availableHeight -> ModifiedSizes(w, h, availableWidth, availableHeight) },
        { _, _, _, _, _, (childWidth, childHeight) ->
            ResolvedComponent(childWidth, childHeight, emptyList()) {}
        }
    )
