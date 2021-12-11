package com.exerro.simpleui.ui

import com.exerro.simpleui.DrawContext
import com.exerro.simpleui.UndocumentedExperimental
import com.exerro.simpleui.WindowEvent

@UndocumentedExperimental
typealias AnyParentContext = ComponentChildrenContext<*, *, *, *, *>

@UndocumentedExperimental
typealias ParentDefinedContext<Model> = ComponentChildrenContext<Model, Float, Float, Nothing?, Nothing?>

@UndocumentedExperimental
typealias ComponentDefinedContext<Model> = ComponentChildrenContext<Model, Nothing?, Nothing?, Float, Float>

@UndocumentedExperimental
typealias ComponentEventHandler = (WindowEvent) -> Boolean

@UndocumentedExperimental
typealias ComponentDrawFunction = DrawContext.() -> Unit
