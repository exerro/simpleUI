package com.exerro.simpleui.ui.modifiers

import com.exerro.simpleui.*
import com.exerro.simpleui.ui.*


@UndocumentedExperimental
fun <ParentWidth: Float?, ParentHeight: Float?, ChildWidth: Float?, ChildHeight: Float?> ParentContext<ParentWidth, ParentHeight, ChildWidth, ChildHeight>.invisible() =
    modifier<ParentWidth, ParentHeight, ChildWidth, ChildHeight, ParentWidth, ParentHeight, ChildWidth, ChildHeight>(
        { w, h, availableWidth, availableHeight -> ModifiedSizes(w, h, availableWidth, availableHeight) },
        { _, _, _, _, _, (childWidth, childHeight, _: DrawContext.() -> Unit) ->
            ResolvedChild(childWidth, childHeight) {
                println("I am called")
            }
        }
    )
