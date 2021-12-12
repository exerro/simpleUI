package com.exerro.simpleui.ui

import com.exerro.simpleui.UndocumentedExperimentalUI

@UndocumentedExperimentalUI
sealed interface WhoDefinesMe

@UndocumentedExperimentalUI
object ParentDefinesMe: WhoDefinesMe

@UndocumentedExperimentalUI
object ChildDefinesMe: WhoDefinesMe

@UndocumentedExperimentalUI
@JvmInline
value class SomeValueForParent<T: WhoDefinesMe> @Deprecated("Use fixForParent* methods to construct this value safely") constructor(
    @Deprecated("Use fixFromChild* methods to access this value safely", level = DeprecationLevel.WARNING)
    val value: Float,
)

@UndocumentedExperimentalUI
@JvmInline
value class SomeValueForChild<T: WhoDefinesMe> @Deprecated("Use fixForChild* methods to construct this value safely") constructor(
    @Deprecated("Use fixFromParent* methods to access this value safely")
    val value: Float,
)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <T: WhoDefinesMe> fixForParentAny(value: Float): SomeValueForParent<T> = SomeValueForParent(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixForParent(value: Float): SomeValueForParent<ChildDefinesMe> = SomeValueForParent(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun nothingForParent(): SomeValueForParent<ParentDefinesMe> = SomeValueForParent(0f)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <T: WhoDefinesMe> fixForChildAny(value: Float): SomeValueForChild<T> = SomeValueForChild(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixForChild(value: Float): SomeValueForChild<ParentDefinesMe> = SomeValueForChild(value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun nothingForChild(): SomeValueForChild<ChildDefinesMe> = SomeValueForChild(0f)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromParentAny(value: SomeValueForChild<*>): Float = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <reified T: WhoDefinesMe> fixFromParentAnyOptional(
    value: SomeValueForChild<T>
): Float? = if (ParentDefinesMe is T) value.value else null

@UndocumentedExperimentalUI
@JvmName("fixFromParentParentDefinesMe")
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromParent(value: SomeValueForChild<ParentDefinesMe>) = value.value

@UndocumentedExperimentalUI
@JvmName("fixFromParentChildDefinesMe")
@Suppress("NOTHING_TO_INLINE")
inline fun fixFromParent(value: SomeValueForChild<ChildDefinesMe>): Float? = null

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromChildAny(value: SomeValueForParent<*>): Float = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun <reified T: WhoDefinesMe> fixFromChildAnyOptional(
    value: SomeValueForParent<T>
): Float? = if (ChildDefinesMe is T) value.value else null

@UndocumentedExperimentalUI
@JvmName("fixFromChildParentDefinesMe")
@Suppress("NOTHING_TO_INLINE")
inline fun fixFromChild(value: SomeValueForParent<ParentDefinesMe>): Float? = null

@UndocumentedExperimentalUI
@JvmName("fixFromChildChildDefinesMe")
@Suppress("DEPRECATION", "NOTHING_TO_INLINE")
inline fun fixFromChild(value: SomeValueForParent<ChildDefinesMe>) = value.value

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> invert(
    value: SomeValueForChild<T>,
    eval: () -> Float
): SomeValueForParent<T> = SomeValueForParent(if (ParentDefinesMe is T) 0f else eval())

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> map(
    value: SomeValueForChild<T>,
    map: (Float) -> Float
): SomeValueForChild<T> = SomeValueForChild(if (ParentDefinesMe is T) map(value.value) else value.value)

@UndocumentedExperimentalUI
@Suppress("DEPRECATION")
inline fun <reified T: WhoDefinesMe> map(
    value: SomeValueForParent<T>,
    map: (Float) -> Float
): SomeValueForParent<T> = SomeValueForParent(if (ChildDefinesMe is T) map(value.value) else value.value)
