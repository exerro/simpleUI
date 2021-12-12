package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimentalUI
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimentalUI
typealias AnyParentContext = ComponentChildrenContext<*, *, *, *, *>

@UndocumentedExperimentalUI
typealias ParentDefinedContext<Model> = ComponentChildrenContext<Model, Float, Float, Nothing?, Nothing?>

@UndocumentedExperimentalUI
typealias ComponentDefinedContext<Model> = ComponentChildrenContext<Model, Nothing?, Nothing?, Float, Float>

@UndocumentedExperimentalUI
typealias ComponentEventHandler = (WindowEvent) -> Boolean

@UndocumentedExperimentalUI
typealias ComponentDrawFunction = DrawContext.() -> Unit
